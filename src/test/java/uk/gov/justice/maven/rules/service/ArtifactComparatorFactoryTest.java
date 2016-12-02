package uk.gov.justice.maven.rules.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;

import java.io.File;

import net.diibadaaba.zipdiff.Differences;
import org.apache.maven.plugin.logging.Log;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class ArtifactComparatorFactoryTest {
    private static final String FILTER = "^((?!file4).)*$";

    @Mock
    private Log log;

    private ArtifactComparatorFactory artifactComparatorFactory = new ArtifactComparatorFactory();
    private File file1;
    private File file2;

    @Before
    public void setup() {
        final ClassLoader classLoader = getClass().getClassLoader();
        file1 = new File(classLoader.getResource("jars/version1.jar").getFile());
        file2 = new File(classLoader.getResource("jars/version2.jar").getFile());
    }


    @Test
    public void shouldProduceArtifactComparatorFromFilterString() throws Exception {
        final ArtifactComparator artifactComparator = artifactComparatorFactory.artifactComparatorOf(FILTER, log);

        final Differences differences = artifactComparator.findDifferences(file1, file2).get();
        assertThat(differences.getAdded().get("file3"), nullValue());
        assertThat(differences.getRemoved().get("file2"), notNullValue());
        assertThat(differences.getChanged().get("file1"), notNullValue());
    }
}