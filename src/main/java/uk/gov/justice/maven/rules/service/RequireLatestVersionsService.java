package uk.gov.justice.maven.rules.service;

import static org.codehaus.plexus.util.StringUtils.isEmpty;

import uk.gov.justice.maven.rules.domain.Artifact;
import uk.gov.justice.maven.rules.domain.ArtifactoryInfo;
import uk.gov.justice.maven.rules.domain.Error;
import uk.gov.justice.maven.rules.domain.RuleException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.diibadaaba.zipdiff.Differences;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

public class RequireLatestVersionsService {

    static final String RAML_MAVEN_PLUGIN = "raml-maven-plugin";
    static final String RAML = "raml";

    private ArtifactoryClient artifactoryClient;
    private ArtifactoryParser parser;
    private MavenProject mavenProject;
    private Log log;
    private ArtifactComparator artifactComparator;

    public RequireLatestVersionsService(ArtifactoryClient artifactoryClient, ArtifactoryParser parser, MavenProject mavenProject,
                                        Log log, ArtifactComparator artifactComparator) {
        this.artifactoryClient = artifactoryClient;
        this.parser = parser;
        this.mavenProject = mavenProject;
        this.log = log;
        this.artifactComparator = artifactComparator;
    }

    public void execute() {
        List<Error> errors = new ArrayList<>();

        ((List<Plugin>) mavenProject.getBuildPlugins()).stream()
                .filter(buildPlugin -> buildPlugin.getArtifactId().equals(RAML_MAVEN_PLUGIN))
                .forEach(plugin -> plugin.getDependencies().stream()
                        .filter(dependency -> (!isEmpty(((Dependency) dependency).getClassifier())) &&
                                ((Dependency) dependency).getClassifier().equals(RAML))
                        .forEach(ramlDependency -> verify((Dependency) ramlDependency)
                                .ifPresent(errors::add)));

        if (errors.size() > 0) {
            throw new RuleException("Rule has failed as found higher released versions of dependencies:\n", errors);
        }
    }

    private Optional<Error> verify(Dependency ramlDependency) {
        log.debug("verifying " + ramlDependency.toString());

        String payload;
        try {
            payload = artifactoryClient.findArtifactInfo(ramlDependency);
        } catch (IOException e) {
            log.error("Couldn't get information from artifactory for " + ramlDependency + ". Error: " + e.getMessage());
            return Optional.empty();
        }

        ArtifactoryInfo artifactVersionList = parser.parse(payload);

        artifactVersionList.getResults().sort(Artifact.reverseComparator);

        Artifact lastReleasedArtifactVersion = artifactVersionList.getResults().get(0);
        Dependency releasedDependency = new Dependency();
        releasedDependency.setGroupId(ramlDependency.getGroupId());
        releasedDependency.setArtifactId(ramlDependency.getArtifactId());
        releasedDependency.setVersion(lastReleasedArtifactVersion.getVersion());

        Optional<File> ramlDependencyFile = downloadArtifact(ramlDependency);
        Optional<File> releasedDependencyFile = downloadArtifact(releasedDependency);

        if (!ramlDependencyFile.isPresent() || !releasedDependencyFile.isPresent()) {
            return Optional.empty();
        }

        if (lastReleasedArtifactVersion.isVersionHigherThan(ramlDependency.getVersion())) {
            Optional<Differences> difference = artifactComparator.findDifferences(ramlDependencyFile.get(), releasedDependencyFile.get(), "META-INF");
            if (difference.isPresent()) {
                return Optional.of(new Error(ramlDependency, lastReleasedArtifactVersion.getVersion(), difference.get()));
            }
        }

        return Optional.empty();
    }

    private Optional<File> downloadArtifact(Dependency ramlDependency) {
        try {
            return Optional.of(artifactoryClient.getArtifact(ramlDependency));
        } catch (IOException e) {
            log.error("Couldn't download artifact: " + ramlDependency + ". Error: " + e.getMessage());
        }
        return Optional.empty();
    }


}
