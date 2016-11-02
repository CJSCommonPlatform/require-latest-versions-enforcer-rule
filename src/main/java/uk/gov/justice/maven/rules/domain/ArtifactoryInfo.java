package uk.gov.justice.maven.rules.domain;

import java.util.ArrayList;
import java.util.List;

public class ArtifactoryInfo {
    private List<Artifact> results = new ArrayList<>();

    public List<Artifact> getResults() {
        return results;
    }

    public void setResults(List<Artifact> results) {
        this.results = results;
    }
}
