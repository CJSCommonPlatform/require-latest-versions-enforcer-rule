package uk.gov.justice.maven.rules;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import uk.gov.justice.maven.rules.service.ArtifactComparator;
import uk.gov.justice.maven.rules.service.ArtifactComparatorFactory;
import uk.gov.justice.maven.rules.service.ArtifactFinder;
import uk.gov.justice.maven.rules.service.ArtifactFinderFactory;
import uk.gov.justice.maven.rules.service.ArtifactVersionCheckingService;
import uk.gov.justice.maven.rules.service.MavenArtifactFinder;

import java.util.List;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.monitor.logging.DefaultLog;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RequireLatestVersionsRuleTest {

    private static final ArtifactResolver NOT_USED_ARTIFACT_RESOLVER = null;
    private static final ArtifactRepository NOT_USED_LOCAL_REPOSITORY = null;
    private static final List<ArtifactRepository> NOT_USED_REMOTE_REPOSITORIES = null;
    private static final String NOT_USED_FILTER = null;
    private static final Log NOT_USED_LOG = null;
    @Mock
    private ArtifactVersionCheckingService artifactVersionCheckingService;

    @Mock
    private EnforcerRuleHelper helper;

    @Mock
    private ArtifactFinderFactory artifactFinderFactory;
    @Mock
    private ArtifactComparatorFactory artifactComparatorFactory;

    @InjectMocks
    private RequireLatestVersionsRule rule = new RequireLatestVersionsRule();

    @Test
    public void shouldExecuteServiceCheckingVersionMismatches() throws Exception {
        final MavenProject mavenProject = new MavenProject();
        final String filter = "someFilter";
        final DefaultLog log = new DefaultLog(null);

        when(helper.evaluate("${project}")).thenReturn(mavenProject);
        when(helper.getLog()).thenReturn(log);
        final ArtifactFinder artifactFinder = new MavenArtifactFinder(NOT_USED_ARTIFACT_RESOLVER, NOT_USED_LOCAL_REPOSITORY, NOT_USED_REMOTE_REPOSITORIES);
        final ArtifactComparator artifactComparator = new ArtifactComparator(NOT_USED_FILTER, NOT_USED_LOG);
        when(artifactFinderFactory.artifactFinderFrom(helper)).thenReturn(artifactFinder);
        when(artifactComparatorFactory.artifactComparatorOf(filter, log)).thenReturn(artifactComparator);

        rule.setFilter(filter);

        rule.execute(helper);
        verify(artifactVersionCheckingService).checkVersionMismatches(mavenProject, artifactFinder, artifactComparator, log);
    }
}