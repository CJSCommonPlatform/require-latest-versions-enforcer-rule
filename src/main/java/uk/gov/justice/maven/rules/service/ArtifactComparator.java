package uk.gov.justice.maven.rules.service;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import net.diibadaaba.zipdiff.DifferenceCalculator;
import net.diibadaaba.zipdiff.Differences;
import org.apache.maven.plugin.logging.Log;

public class ArtifactComparator {

    private Log log;
    private String filter;

    public ArtifactComparator(Log log, String filter) {
        this.log = log;
        this.filter = filter;
    }

    public Optional<Differences> findDifferences(File ramlDependencyFile, File releasedDependencyFile) {
        Differences differences = null;

        try {
            DifferenceCalculator calc = new DifferenceCalculator(ramlDependencyFile, releasedDependencyFile);
            calc.setIgnoreTimestamps(true);
            differences = calc.getDifferences();

            if (differences.hasDifferences()) {
                differences.getAdded().clear();
                new MapFilter<>(differences.getChanged()).apply(filter);
                new MapFilter<>(differences.getRemoved()).apply(filter);
            }
        } catch (IOException e) {
            log.error("Couldn't read files: " + ramlDependencyFile + "," + releasedDependencyFile, e);
        }

        return differences != null && differences.hasDifferences() ?
                Optional.of(differences) :
                Optional.empty();
    }

}
