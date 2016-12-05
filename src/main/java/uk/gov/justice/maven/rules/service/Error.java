package uk.gov.justice.maven.rules.service;

import net.diibadaaba.zipdiff.Differences;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.maven.model.Dependency;

//todo add tests
public class Error {

    private Dependency ramlDependency;
    private String releasedVersion;
    private Differences differences;

    public Error(Dependency ramlDependency, String artifactVersion, Differences differences) {
        this.ramlDependency = ramlDependency;
        this.releasedVersion = artifactVersion;
        this.differences = differences;
    }

    @Override
    public String toString() {
        return "\nError{" +
                "ramlDependency=" + ramlDependency +
                ", releasedVersion='" + releasedVersion + '\'' +
                ", differences:\n'" + toString(differences) +
                '}';
    }

    public String toString(Differences differences) {
        StringBuffer sb = new StringBuffer();

        if (differences.getAdded().size() != 0) {
            sb.append(differences.getAdded().size() + " files were");
            sb.append(" added to " + differences.getFilename2() + "\n");
            differences.getAdded().keySet().forEach(key -> sb.append("\t[added] " + key + "\n"));
        }

        if (differences.getRemoved().size() != 0) {
            sb.append(differences.getRemoved().size() + " files were");
            sb.append(" removed from " + differences.getFilename2() + "\n");
            differences.getRemoved().keySet().forEach(key -> sb.append("\t[removed] " + key + "\n"));
        }

        if (differences.getChanged().size() != 0) {
            sb.append(differences.getChanged().size() + " files changed\n");
            differences.getChanged().keySet().forEach(key -> {
                ZipArchiveEntry[] entries = differences.getChanged().get(key);
                sb.append("\t[changed] " + key + " ")
                        .append(" ( size " + entries[0].getSize())
                        .append(" : " + entries[1].getSize())
                        .append(" )\n");
            });
        }

        int differenceCount = differences.getAdded().size() + differences.getChanged().size() + differences.getRemoved().size();

        sb.append("Total differences: " + differenceCount);

        return sb.toString();
    }
}
