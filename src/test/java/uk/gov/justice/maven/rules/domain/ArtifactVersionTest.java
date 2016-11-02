package uk.gov.justice.maven.rules.domain;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.core.Is.is;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class ArtifactVersionTest {

    @Test
    public void testIsVersionHigherThan() throws Exception {
        assertThat(new Artifact("1.0.0").isVersionHigherThan("2.0.1"), is(false));
        assertThat(new Artifact("3.0.0").isVersionHigherThan("2.0.1"), is(true));
        assertThat(new Artifact("1").isVersionHigherThan("0.9.1.0.2"), is(true));
    }

    @Test
    public void testEquals(){
        assertThat(new Artifact("1.0.0").equals(new Artifact("1.0.0")), is(true));
        assertThat(new Artifact("1.0.1").equals(new Artifact("1.0.0")), is(false));
    }

    @Test
    public void testSorting() {
        List<Artifact> artifacts = Arrays.asList(new Artifact("1.0.1"), new Artifact("2.0.1"), new Artifact("1.0.2"), new Artifact("1.1.3"));

        artifacts.sort(Artifact.reverseComparator);

        assertThat(artifacts, contains(new Artifact("2.0.1"), new Artifact("1.1.3"), new Artifact("1.0.2"), new Artifact("1.0.1")));
    }

}