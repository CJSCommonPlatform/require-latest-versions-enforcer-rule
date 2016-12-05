package uk.gov.justice.maven.rules.service;

import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

import net.diibadaaba.zipdiff.Differences;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ArtifactVersionCheckingServiceTest {

    private ArtifactVersionCheckingService apiConvergenceService = new ArtifactVersionCheckingService();

    @Mock
    private ArtifactFinder artifactFinder;

    @Mock
    private ArtifactComparator artifactComparator;

    @Mock
    private Log log;

    @Mock
    private File file;
    @Mock
    private File file2;

    @Mock
    private MavenProject mavenProject;

    @Test
    public void executeWithNoPluginsFound() throws ExpressionEvaluationException, EnforcerRuleException {
        when(mavenProject.getBuildPlugins()).thenReturn(noPlugins());

        apiConvergenceService.checkVersionMismatches(mavenProject, artifactFinder, artifactComparator, log);

        verifyZeroInteractions(artifactFinder);
    }

    @Test
    public void executeWithRamlMavenPluginWithNoDepsFound() throws ExpressionEvaluationException, EnforcerRuleException {
        Plugin plugin = ramlPlugin();
        when(mavenProject.getBuildPlugins()).thenReturn(asList(plugin));

        apiConvergenceService.checkVersionMismatches(mavenProject, artifactFinder, artifactComparator, log);

        verifyZeroInteractions(artifactFinder);
    }

    @Test
    public void executeWithRamlMavenPluginWithDepsWithoutClassifier() throws ExpressionEvaluationException, EnforcerRuleException {
        Plugin ramlPlugin = ramlPlugin();
        when(mavenProject.getBuildPlugins()).thenReturn(asList(ramlPlugin));

        ramlPlugin.addDependency(dependencyWithVersion("1.1"));

        apiConvergenceService.checkVersionMismatches(mavenProject, artifactFinder, artifactComparator, log);

        verifyZeroInteractions(artifactFinder);
    }

    @Test
    public void executeWithRamlMavenPluginWithRamlDepsWithVersionsUpToDate() throws ExpressionEvaluationException, EnforcerRuleException {
        Plugin ramlPlugin = ramlPlugin();
        when(mavenProject.getBuildPlugins()).thenReturn(asList(ramlPlugin));

        Dependency dependency = dependencyWithVersion("3.2.1");
        dependency.setClassifier("raml");
        ramlPlugin.addDependency(dependency);

        when(artifactFinder.latestArtifactOf(dependency)).thenReturn(artifactOf("2.0.188"));

        apiConvergenceService.checkVersionMismatches(mavenProject, artifactFinder, artifactComparator, log);
    }

    @Test
    public void shouldPassWhenLastVersionNotFound() throws Exception {
        Plugin ramlPlugin = ramlPlugin();
        when(mavenProject.getBuildPlugins()).thenReturn(asList(ramlPlugin));

        Dependency dependency = dependencyWithVersion("3.2.1");
        dependency.setClassifier("raml");
        ramlPlugin.addDependency(dependency);

        when(artifactFinder.latestArtifactOf(dependency)).thenReturn(empty());

        apiConvergenceService.checkVersionMismatches(mavenProject, artifactFinder, artifactComparator, log);
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void executeWithRamlMavenPluginWithRamlDepsWithVersionsNotUpToDate() throws Exception {
        Plugin ramlPlugin = ramlPlugin();
        when(mavenProject.getBuildPlugins()).thenReturn(asList(ramlPlugin));

        final String version = "2.0.187";
        Dependency dependency = dependencyWithVersion(version);
        dependency.setClassifier("raml");
        ramlPlugin.addDependency(dependency);

        thrown.expect(RuleException.class);
        thrown.expectMessage("Rule has failed as found higher released versions of dependencies:");


        final String latestVersion = "2.0.188";
        when(artifactFinder.latestArtifactOf(dependency)).thenReturn(artifactOf(latestVersion, file2));

        when(artifactFinder.artifactOf(dependency)).thenReturn(artifactOf(version, file));

        when(artifactComparator.findDifferences(file, file2)).thenReturn(Optional.of(new Differences()));

        apiConvergenceService.checkVersionMismatches(mavenProject, artifactFinder, artifactComparator, log);
    }

    @Test
    public void executeWithRamlMavenPluginWithRamlDepsWithVersionsNotUpToDateButSnapshotsAreIgnored() throws Exception {
        Plugin ramlPlugin = ramlPlugin();
        when(mavenProject.getBuildPlugins()).thenReturn(asList(ramlPlugin));

        Dependency dependency = dependencyWithVersion("2.0.187-SNAPSHOT");
        dependency.setClassifier("raml");
        ramlPlugin.addDependency(dependency);

        when(artifactFinder.latestArtifactOf(dependency)).thenReturn(artifactOf("2.0.188"));

        apiConvergenceService.checkVersionMismatches(mavenProject, artifactFinder, artifactComparator, log);
    }

    private Optional<Artifact> artifactOf(final String version) {
        return Optional.of(new DefaultArtifact("a", "a", version, "a", "a", "a", null));
    }

    private Optional<Artifact> artifactOf(final String version, final File file) {
        final DefaultArtifact artifact = new DefaultArtifact("not_used", "not_used", version, "not_used", "not_used", "not_used", null);
        artifact.setFile(file);
        return Optional.of(artifact);
    }


    private Plugin ramlPlugin() {
        Plugin plugin = new Plugin();
        plugin.setArtifactId("raml-maven-plugin");
        return plugin;
    }

    private Dependency dependencyWithVersion(String version) {
        Dependency ramlDep = new Dependency();
        ramlDep.setArtifactId("a");
        ramlDep.setVersion(version);
        ramlDep.setGroupId("xyz");
        return ramlDep;
    }


    private ArrayList noPlugins() {
        return new ArrayList();
    }


}