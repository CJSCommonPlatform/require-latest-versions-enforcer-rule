package uk.gov.justice.maven.rules.domain;

import java.util.Comparator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Artifact {
    private String version;

    public Artifact(String version) {
        this.version = version;
    }

    public Artifact() {
        //needed for JSON deserializer
    }

    public String getVersion() {
        return version;
    }

    public boolean isVersionHigherThan(String otherArtifactVersion) {
        return reverseComparator.compare(new Artifact(otherArtifactVersion), this) > 0;
    }

    @Override
    public boolean equals(Object otherArtifactVersion) {
        return this.version.equals(((Artifact) otherArtifactVersion).getVersion());
    }

    public static Comparator<Artifact> reverseComparator = (Artifact o1, Artifact o2) ->
            new DefaultArtifactVersion(o2.getVersion()).compareTo(new DefaultArtifactVersion(o1.getVersion()));

    @Override
    public String toString() {
        return "Artifact{" +
                "version='" + version + '\'' +
                '}';
    }
}
