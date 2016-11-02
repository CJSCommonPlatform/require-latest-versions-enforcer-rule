package uk.gov.justice.maven.rules.domain;

import org.apache.maven.model.Dependency;

public class Error {

    private Dependency ramlDependency;
    private String releasedVersion;

    public Error(Dependency ramlDependency, String artifactVersion) {
        this.ramlDependency = ramlDependency;
        this.releasedVersion = artifactVersion;
    }

    public Dependency getRamlDependency() {
        return ramlDependency;
    }

    public String getReleasedVersion() {
        return releasedVersion;
    }

    @Override
    public String toString() {
        return "Error{" +
                "ramlDependency=" + ramlDependency +
                ", releasedVersion='" + releasedVersion + '\'' +
                '}';
    }
}
