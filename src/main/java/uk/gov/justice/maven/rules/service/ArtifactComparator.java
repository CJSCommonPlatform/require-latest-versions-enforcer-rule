package uk.gov.justice.maven.rules.service;

import static java.util.Optional.empty;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import net.diibadaaba.zipdiff.DifferenceCalculator;
import net.diibadaaba.zipdiff.Differences;
import org.apache.maven.plugin.logging.Log;

public class ArtifactComparator {

    private final Log log;
    private final String filter;

    public ArtifactComparator(final String filter, final Log log) {
        this.log = log;
        this.filter = filter;
    }

    public Optional<Differences> findDifferences(final File ramlDependencyFile, final File releasedDependencyFile) {

        try {
            final DifferenceCalculator calc = new DifferenceCalculator(ramlDependencyFile, releasedDependencyFile);
            calc.setIgnoreTimestamps(true);
            final Differences differences = calc.getDifferences();

            if (differences.hasDifferences()) {
                differences.getAdded().clear();
                new MapFilter<>(differences.getChanged()).apply(filter);
                new MapFilter<>(differences.getRemoved()).apply(filter);
            }

            return differences.hasDifferences() ? Optional.of(differences) : empty();

        } catch (final IOException e) {
            log.error("Couldn't read files: " + ramlDependencyFile + "," + releasedDependencyFile, e);
        }

        return empty();
    }

}
