package uk.gov.justice.maven.rules.service;

import static java.util.Optional.empty;
import static org.codehaus.plexus.util.StringUtils.isEmpty;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import net.diibadaaba.zipdiff.Differences;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

public class ArtifactVersionCheckingService {

    private static final Comparator<String> VERSION_COMPARATOR = Comparator.comparing(DefaultArtifactVersion::new);
    private static final String RAML_MAVEN_PLUGIN = "raml-maven-plugin";
    private static final String RAML = "raml";
    private static final String SNAPSHOT = "SNAPSHOT";

    public void checkVersionMismatches(final MavenProject mavenProject, final ArtifactFinder artifactFinder,
                                       final ArtifactComparator artifactComparator, final Log log) {
        final List<Error> errors = new ArrayList<>();

        mavenProject.getBuildPlugins().stream()
                .filter(buildPlugin -> buildPlugin.getArtifactId().equals(RAML_MAVEN_PLUGIN))
                .forEach(plugin -> plugin.getDependencies().stream()
                        .filter(dependency -> !isEmpty((dependency).getClassifier()) &&
                                dependency.getClassifier().equals(RAML))
                        .filter(dependency -> !isEmpty((dependency).getVersion()) &&
                                !dependency.getVersion().endsWith(SNAPSHOT))
                        .forEach(ramlDependency -> versionMismatchError(ramlDependency, artifactFinder, artifactComparator, log)
                                .ifPresent(errors::add)));

        if (errors.size() > 0) {
            throw new RuleException("Rule has failed as found higher released versions of dependencies:\n", errors);
        }
    }

    private Optional<Error> versionMismatchError(final Dependency dependency, final ArtifactFinder artifactFinder, final ArtifactComparator artifactComparator, final Log log) {
        log.debug("verifying " + dependency.toString());

        final Optional<Artifact> lastReleasedArtifact = artifactFinder.latestArtifactOf(dependency);

        if (lastReleasedArtifact.isPresent() &&
                isVersionHigherThan(lastReleasedArtifact.get().getVersion(), dependency.getVersion())) {

            final Optional<Artifact> artifact = artifactFinder.artifactOf(dependency);
            if (!artifact.isPresent()) {
                return empty();
            }
            final Optional<Differences> difference = artifactComparator.findDifferences(artifact.get().getFile(), lastReleasedArtifact.get().getFile());
            if (difference.isPresent()) {
                return Optional.of(new Error(dependency, lastReleasedArtifact.get().getVersion(), difference.get()));
            }
        }
        return empty();
    }

    private boolean isVersionHigherThan(final String version1, final String version2) {
        return VERSION_COMPARATOR.compare(version1, version2) > 0;
    }


}
