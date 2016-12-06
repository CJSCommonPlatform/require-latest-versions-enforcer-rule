package uk.gov.justice.maven.rules.service;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.MavenArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.model.Dependency;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ArtifactFinderFactoryTest {

    @Mock
    private EnforcerRuleHelper helper;

    @Mock
    private ArtifactResolver resolver;

    private ArtifactFinderFactory factory = new ArtifactFinderFactory();

    @Test
    public void shouldProduceArtifactFinderWithResolverAndRepositories() throws Exception {
        final ArtifactRepository localRepository = new MavenArtifactRepository();
        final List<ArtifactRepository> remoteRepositories = asList(new MavenArtifactRepository());

        when(helper.evaluate("${localRepository}")).thenReturn(localRepository);
        when(helper.evaluate("${project.remoteArtifactRepositories}")).thenReturn(remoteRepositories);
        when(helper.getComponent(ArtifactResolver.class)).thenReturn(resolver);
        when(resolver.resolve(any(ArtifactResolutionRequest.class))).thenReturn(new ArtifactResolutionResult());


        final ArtifactFinder artifactFinder = factory.artifactFinderFrom(helper);
        assertThat(artifactFinder, instanceOf(MavenArtifactFinder.class));

        artifactFinder.artifactOf(dummyDependency());

        verifyDependencyResolvedUsing(resolver, localRepository, remoteRepositories);


    }

    private void verifyDependencyResolvedUsing(final ArtifactResolver artifactResolver, final ArtifactRepository localRepository, final List<ArtifactRepository> remoteRepositories) {
        ArgumentCaptor<ArtifactResolutionRequest> resolutionRequestCaptor = ArgumentCaptor.forClass(ArtifactResolutionRequest.class);
        verify(artifactResolver).resolve(resolutionRequestCaptor.capture());
        final ArtifactResolutionRequest resolutionRequest = resolutionRequestCaptor.getValue();

        assertThat(resolutionRequest.getLocalRepository(), is(localRepository));
        assertThat(resolutionRequest.getRemoteRepositories(), is(remoteRepositories));
    }

    private Dependency dummyDependency() {
        final Dependency dependency = new Dependency();
        dependency.setArtifactId("NOT_USED");
        dependency.setGroupId("NOT_USED");
        dependency.setVersion("NOT_USED");
        return dependency;
    }
}
