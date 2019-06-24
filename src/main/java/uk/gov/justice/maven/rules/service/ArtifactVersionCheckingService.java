package uk.gov.justice.maven.rules.service;

import static java.util.Comparator.comparing;
import static org.codehaus.plexus.util.StringUtils.isEmpty;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

public class ArtifactVersionCheckingService {

    private static final Comparator<String> VERSION_COMPARATOR = comparing(DefaultArtifactVersion::new);
    private static final String RAML = "raml";
    private static final String SNAPSHOT = "SNAPSHOT";

    private final CheckablePlugins checkablePlugins;

    public ArtifactVersionCheckingService(final CheckablePlugins checkablePlugins) {
        this.checkablePlugins = checkablePlugins;
    }

    public void checkVersionMismatches(final MavenProject mavenProject,
                                       final ArtifactFinder artifactFinder,
                                       final ArtifactComparator artifactComparator,
                                       final Log log) {

        final List<Error> errors = new ArrayList<>();

        mavenProject.getBuildPlugins().stream()
                .filter(checkablePlugins::isCheckablePlugin)
                .forEach(plugin -> plugin.getDependencies().stream()
                        .filter(this::isRamlClassifierPresent)
                        .filter(this::isNotSnapshot)
                        .forEach(ramlDependency -> versionMismatchError(ramlDependency, artifactFinder, artifactComparator, log)
                                .ifPresent(errors::add)));

        if (!errors.isEmpty()) {
            throw new RuleException("Rule has failed as found higher released versions of dependencies:\n", errors);
        }
    }

    private boolean isRamlClassifierPresent(final Dependency dependency) {

        return !isEmpty((dependency).getClassifier()) &&
                dependency.getClassifier().equals(RAML);
    }

    private boolean isNotSnapshot(final Dependency dependency) {

        return !isEmpty((dependency).getVersion()) &&
                !dependency.getVersion().endsWith(SNAPSHOT);
    }

    private Optional<Error> versionMismatchError(final Dependency dependency,
                                                 final ArtifactFinder artifactFinder,
                                                 final ArtifactComparator artifactComparator,
                                                 final Log log) {

        log.debug("verifying " + dependency.toString());

        return artifactFinder.latestArtifactOf(dependency)
                .filter(isLastReleaseVersionGreaterThanVersionOf(dependency)::apply)
                .flatMap(lastReleasedArtifact -> artifactFinder.artifactOf(dependency)
                        .flatMap(artifact -> artifactComparator.findDifferences(artifact.getFile(), lastReleasedArtifact.getFile())
                                .map(difference -> new Error(dependency, lastReleasedArtifact.getVersion(), difference))));
    }

    private Function<Artifact, Boolean> isLastReleaseVersionGreaterThanVersionOf(final Dependency dependency) {

        return (lastReleasedArtifact) -> {

            final String lastReleasedVersion = lastReleasedArtifact.getVersion();
            final String currentVersion = dependency.getVersion();

            return VERSION_COMPARATOR.compare(lastReleasedVersion, currentVersion) > 0;
        };
    }
}
