package uk.gov.justice.maven.rules.service;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.justice.maven.rules.service.RequireLatestVersionsService.RAML;
import static uk.gov.justice.maven.rules.utils.Exceptions.assertThat;

import uk.gov.justice.maven.rules.domain.Artifact;
import uk.gov.justice.maven.rules.domain.ArtifactoryInfo;
import uk.gov.justice.maven.rules.domain.RuleException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RequireLatestVersionsServiceTest {

    RequireLatestVersionsService apiConvergenceService;

    @Mock
    private ArtifactoryClient artifactoryClient;

    @Mock
    private ArtifactoryParser artifactoryParser;

    @Mock
    private EnforcerRuleHelper helper;

    @Mock
    private Log log;

    @Mock
    private MavenProject mavenProject;


    @Before
    public void setUp() throws Exception {
        when(helper.evaluate(Matchers.anyString())).thenReturn(mavenProject);
        when(helper.getLog()).thenReturn(log);
        apiConvergenceService = new RequireLatestVersionsService(artifactoryClient, artifactoryParser, helper);
    }

    @Test
    public void executeWithNoPluginsFound() throws ExpressionEvaluationException, EnforcerRuleException {
        when(mavenProject.getBuildPlugins()).thenReturn(noPlugins());

        apiConvergenceService.execute();

        verifyZeroInteractions(artifactoryParser, artifactoryClient);
    }

    @Test
    public void executeWithRamlMavenPluginWithNoDepsFound() throws ExpressionEvaluationException, EnforcerRuleException {
        Plugin plugin = ramlPlugin();
        when(mavenProject.getBuildPlugins()).thenReturn(asList(plugin));

        apiConvergenceService.execute();

        verifyZeroInteractions(artifactoryParser, artifactoryClient);
    }

    @Test
    public void executeWithRamlMavenPluginWithDepsWithoutClassifier() throws ExpressionEvaluationException, EnforcerRuleException {
        Plugin ramlPlugin = ramlPlugin();
        when(mavenProject.getBuildPlugins()).thenReturn(asList(ramlPlugin));

        ramlPlugin.addDependency(dependencyWithVersion("1.1"));

        verifyZeroInteractions(artifactoryParser, artifactoryClient);
    }

    @Test
    public void executeWithRamlMavenPluginWithRamlDepsWithVersionsUpToDate() throws ExpressionEvaluationException, EnforcerRuleException {
        Plugin ramlPlugin = ramlPlugin();
        when(mavenProject.getBuildPlugins()).thenReturn(asList(ramlPlugin));

        Dependency dependency = dependencyWithVersion("3.2.1");
        dependency.setClassifier(RAML);
        ramlPlugin.addDependency(dependency);

        when(artifactoryParser.parse(anyString())).thenReturn(artifactsWithVersions("2.0.187", "2.0.188"));

        apiConvergenceService.execute();
    }

    @Test
    public void executeWithRamlMavenPluginWithRamlDepsWithVersionsNotUpToDate() throws Exception {
        Plugin ramlPlugin = ramlPlugin();
        when(mavenProject.getBuildPlugins()).thenReturn(asList(ramlPlugin));

        Dependency dependency = dependencyWithVersion("2.0.187");
        dependency.setClassifier(RAML);
        ramlPlugin.addDependency(dependency);

        when(artifactoryParser.parse(anyString())).thenReturn(artifactsWithVersions("2.0.187", "2.0.188"));

        assertThat(() -> apiConvergenceService.execute())
                .throwsException(RuleException.class)
                .withMessageContaining("Rule has failed as found higher released versions of dependencies.");
    }

    private Plugin ramlPlugin() {
        Plugin plugin = new Plugin();
        plugin.setArtifactId(RequireLatestVersionsService.RAML_MAVEN_PLUGIN);
        return plugin;
    }

    private Dependency dependencyWithVersion(String version) {
        Dependency ramlDep = new Dependency();
        ramlDep.setArtifactId("a");
        ramlDep.setVersion(version);
        ramlDep.setGroupId("xyz");
        return ramlDep;
    }

    private ArtifactoryInfo artifactsWithVersions(String... versions) {
        List<Artifact> list = new ArrayList();
        Arrays.stream(versions).forEach(version -> list.add(new Artifact(version)));

        ArtifactoryInfo artifactoryInfo = new ArtifactoryInfo();
        artifactoryInfo.setResults(list);
        return artifactoryInfo;
    }

    private ArrayList noPlugins() {
        return new ArrayList();
    }


}