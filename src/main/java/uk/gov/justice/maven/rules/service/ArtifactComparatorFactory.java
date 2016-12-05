package uk.gov.justice.maven.rules.service;

import org.apache.maven.plugin.logging.Log;

public class ArtifactComparatorFactory {
    public ArtifactComparator artifactComparatorOf(final String filter, final Log log) {
        return new ArtifactComparator(filter, log);
    }


}
