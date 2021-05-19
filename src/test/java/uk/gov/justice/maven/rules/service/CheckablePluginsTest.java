package uk.gov.justice.maven.rules.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.apache.maven.model.Plugin;
import org.junit.Test;

public class CheckablePluginsTest {

    private CheckablePlugins checkablePlugins = new CheckablePlugins();

    @Test
    public void shouldMatchRamlMavenPlugin() {

        final boolean result = checkablePlugins.isCheckablePlugin(pluginWithArtifactIdOf("raml-maven-plugin"));

        assertThat(result, is(true));
    }

    @Test
    public void shouldMatchGeneratorPlugin() {

        final boolean result = checkablePlugins.isCheckablePlugin(pluginWithArtifactIdOf("generator-plugin"));

        assertThat(result, is(true));
    }

    @Test
    public void shouldMatchDirectClientGeneratorPlugin() {

        final boolean result = checkablePlugins.isCheckablePlugin(pluginWithArtifactIdOf("direct-client-generator-plugin"));

        assertThat(result, is(true));
    }

    @Test
    public void shouldMatchMessagingAdapterGeneratorPlugin() {

        final boolean result = checkablePlugins.isCheckablePlugin(pluginWithArtifactIdOf("messaging-adapter-generator-plugin"));

        assertThat(result, is(true));
    }

    @Test
    public void shouldMatchMessagingClientGeneratorPlugin() {

        final boolean result = checkablePlugins.isCheckablePlugin(pluginWithArtifactIdOf("messaging-client-generator-plugin"));

        assertThat(result, is(true));
    }

    @Test
    public void shouldMatchGRestAdapterGeneratorPlugin() {

        final boolean result = checkablePlugins.isCheckablePlugin(pluginWithArtifactIdOf("rest-adapter-generator-plugin"));

        assertThat(result, is(true));
    }

    @Test
    public void shouldMatchRestClientGeneratorPlugin() {

        final boolean result = checkablePlugins.isCheckablePlugin(pluginWithArtifactIdOf("rest-client-generator-plugin"));

        assertThat(result, is(true));
    }

    @Test
    public void shouldMatchUnifiedsearchClientGeneratorPlugin() {

        final boolean result = checkablePlugins.isCheckablePlugin(pluginWithArtifactIdOf("unifiedsearch-client-generator-plugin"));

        assertThat(result, is(true));
    }

    @Test
    public void shouldNotMatchUnknownPlugin() {

        final boolean result = checkablePlugins.isCheckablePlugin(pluginWithArtifactIdOf("unknown-plugin"));

        assertThat(result, is(false));
    }

    private Plugin pluginWithArtifactIdOf(final String artifactId) {

        final Plugin plugin = new Plugin();
        plugin.setArtifactId(artifactId);
        return plugin;
    }
}