package uk.gov.justice.maven.rules.service;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.MavenArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.model.Dependency;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MavenArtifactFinderTest {

    private ArtifactRepository localRepository;

    private List<ArtifactRepository> remoteRepositories;

    @Mock
    private ArtifactResolver artifactResolver;


    @Mock
    private ArtifactFinder artifactFinder;

    @Before
    public void setUp() throws Exception {
        localRepository = new MavenArtifactRepository();
        remoteRepositories = asList(new MavenArtifactRepository());

        artifactFinder = new MavenArtifactFinder(artifactResolver, localRepository, remoteRepositories);
    }

    @Test
    public void shouldPassRequestToArtifactResolver() throws Exception {

        final String artifactId = "artifact.abc";
        final String groupId = "group.123";
        final String classifier = "someClassifier";
        final String scope = "someScope";
        final String type = "someType";

        final Dependency dependency = new Dependency();
        dependency.setArtifactId(artifactId);
        dependency.setGroupId(groupId);
        dependency.setClassifier(classifier);
        dependency.setScope(scope);
        dependency.setType(type);

        when(artifactResolver.resolve(any(ArtifactResolutionRequest.class))).thenReturn(new ArtifactResolutionResult());


        artifactFinder.latestArtifactOf(dependency);

        ArgumentCaptor<ArtifactResolutionRequest> resolutionRequestCaptor = ArgumentCaptor.forClass(ArtifactResolutionRequest.class);
        verify(artifactResolver).resolve(resolutionRequestCaptor.capture());

        final ArtifactResolutionRequest resolutionRequest = resolutionRequestCaptor.getValue();

        assertThat(resolutionRequest.getLocalRepository(), is(localRepository));
        assertThat(resolutionRequest.getRemoteRepositories(), is(remoteRepositories));

        final Artifact artifactToResolve = resolutionRequest.getArtifact();

        assertThat(artifactToResolve.getGroupId(), is(groupId));
        assertThat(artifactToResolve.getArtifactId(), is(artifactId));
        assertThat(artifactToResolve.getVersion(), is("LATEST"));
        assertThat(artifactToResolve.getClassifier(), is(classifier));
        assertThat(artifactToResolve.getType(), is(type));
        assertThat(artifactToResolve.getScope(), is(scope));

    }

    @Test
    public void shouldReturnResolvedArtifact() throws Exception {
        final String groupId = "group.1234";
        final String artifactId = "artifact.abcd";
        final String classifier = "someClassifier1";
        final String scope = "someScope";
        final String type = "someType";
        final String version = "1.1";


        final ArtifactResolutionResult resolutionResult = new ArtifactResolutionResult();
        final Artifact resolvedArtifact = new DefaultArtifact(groupId, artifactId, version, scope, type, classifier, null);
        resolutionResult.setArtifacts(singleton(resolvedArtifact));

        when(artifactResolver.resolve(any(ArtifactResolutionRequest.class))).thenReturn(resolutionResult);

        final Dependency dependency = new Dependency();
        dependency.setArtifactId(artifactId);
        dependency.setGroupId(groupId);
        dependency.setClassifier(classifier);
        dependency.setScope(scope);
        dependency.setType(type);
        assertThat(artifactFinder.latestArtifactOf(dependency).get(), is(resolvedArtifact));
    }
}