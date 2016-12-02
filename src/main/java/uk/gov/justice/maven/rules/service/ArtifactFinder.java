package uk.gov.justice.maven.rules.service;


import java.util.Optional;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;

public interface ArtifactFinder {

    Optional<Artifact> latestArtifactOf(Dependency dependency);

    Optional<Artifact> artifactOf(Dependency dependency);
}
