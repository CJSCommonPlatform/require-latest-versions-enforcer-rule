package uk.gov.justice.maven.rules.service;

import org.apache.maven.model.Dependency;

public class ArtifactUrlBuilder {

    public String build(Dependency dependency) {
        String group = dependency.getGroupId().replaceAll("\\.", "\\/");
        String artifactId = dependency.getArtifactId();
        String version = dependency.getVersion();
        return group + "/" + artifactId + "/" + version + "/" + artifactId + "-" + version + "-" + "raml.jar";
    }
}
