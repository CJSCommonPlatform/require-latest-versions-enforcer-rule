package uk.gov.justice.maven.rules.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import net.diibadaaba.zipdiff.DifferenceCalculator;
import net.diibadaaba.zipdiff.Differences;
import org.apache.maven.plugin.logging.Log;

public class ArtifactComparator {

    private Log log;

    public ArtifactComparator(Log log) {
        this.log = log;
    }

    public Optional<Differences> findDifferences(File ramlDependencyFile, File releasedDependencyFile, List<String> filters) {
        Differences differences = null;

        try {
            DifferenceCalculator calc = new DifferenceCalculator(ramlDependencyFile, releasedDependencyFile);
            calc.setIgnoreTimestamps(true);
            differences = calc.getDifferences();

            if (differences.hasDifferences()) {
                new MapFilter<>(differences.getAdded()).apply(filters);
                new MapFilter<>(differences.getChanged()).apply(filters);
                new MapFilter<>(differences.getRemoved()).apply(filters);
            }
        } catch (IOException e) {
            log.error("Couldn't read files: " + ramlDependencyFile + "," + releasedDependencyFile, e);
        }

        return differences != null && differences.hasDifferences() ?
                Optional.of(differences) :
                Optional.empty();
    }

}
