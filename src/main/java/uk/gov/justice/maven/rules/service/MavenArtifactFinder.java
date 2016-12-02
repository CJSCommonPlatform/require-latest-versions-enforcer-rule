package uk.gov.justice.maven.rules.service;



import java.util.List;
import java.util.Optional;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.model.Dependency;

public class MavenArtifactFinder implements ArtifactFinder {
    private static final String LATEST_VERSION = "LATEST";
    private final ArtifactResolver artifactResolver;
    private final ArtifactRepository localRepository;
    private final List<ArtifactRepository> remoteRepositories;

    public MavenArtifactFinder(final ArtifactResolver artifactResolver,
                               final ArtifactRepository localRepository,
                               final List<ArtifactRepository> remoteRepositories) {
        this.artifactResolver = artifactResolver;
        this.localRepository = localRepository;
        this.remoteRepositories = remoteRepositories;
    }

    @Override
    public Optional<Artifact> latestArtifactOf(final Dependency dependency) {
        final Dependency dependencyClone = dependency.clone();
        dependencyClone.setVersion(LATEST_VERSION);
        return artifactOf(dependencyClone);
    }

    @Override
    public Optional<Artifact> artifactOf(final Dependency dependency) {
        final ArtifactResolutionRequest artifactResolutionRequest = new ArtifactResolutionRequest();
        artifactResolutionRequest.setLocalRepository(localRepository);
        artifactResolutionRequest.setRemoteRepositories(remoteRepositories);
        artifactResolutionRequest.setArtifact(new DefaultArtifact(dependency.getGroupId(),
                dependency.getArtifactId(), dependency.getVersion(), dependency.getScope(), dependency.getType(),
                dependency.getClassifier(), new DefaultArtifactHandler(dependency.getType())));

        return artifactResolver.resolve(artifactResolutionRequest).getArtifacts().stream().findFirst();
    }
}
