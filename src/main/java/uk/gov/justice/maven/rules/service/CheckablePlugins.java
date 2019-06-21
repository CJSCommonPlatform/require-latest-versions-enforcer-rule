package uk.gov.justice.maven.rules.service;

import org.apache.maven.model.Plugin;

public class CheckablePlugins {

    private static final String RAML_MAVEN_PLUGIN = "raml-maven-plugin";
    private static final String GENERATOR_PLUGIN = "generator-plugin";
    private static final String DIRECT_CLIENT_GENERATOR_PLUGIN = "direct-client-generator-plugin";
    private static final String MESSAGING_ADAPTER_GENERATOR_PLUGIN = "messaging-adapter-generator-plugin";
    private static final String MESSAGING_CLIENT_GENERATOR_PLUGIN = "messaging-client-generator-plugin";
    private static final String REST_ADAPTER_GENERATOR_PLUGIN = "rest-adapter-generator-plugin";
    private static final String REST_CLIENT_GENERATOR_PLUGIN = "rest-client-generator-plugin";
    private static final String UNIFIEDSEARCH_CLIENT_GENERATOR_PLUGIN = "unifiedsearch-client-generator-plugin";

    public boolean isCheckablePlugin(final Plugin buildPlugin) {

        final String artifactId = buildPlugin.getArtifactId();

        return artifactId.equals(RAML_MAVEN_PLUGIN) ||
                artifactId.equals(GENERATOR_PLUGIN) ||
                artifactId.equals(DIRECT_CLIENT_GENERATOR_PLUGIN) ||
                artifactId.equals(MESSAGING_ADAPTER_GENERATOR_PLUGIN) ||
                artifactId.equals(MESSAGING_CLIENT_GENERATOR_PLUGIN) ||
                artifactId.equals(REST_ADAPTER_GENERATOR_PLUGIN) ||
                artifactId.equals(REST_CLIENT_GENERATOR_PLUGIN) ||
                artifactId.equals(UNIFIEDSEARCH_CLIENT_GENERATOR_PLUGIN);
    }
}
