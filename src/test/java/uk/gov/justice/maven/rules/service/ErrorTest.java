package uk.gov.justice.maven.rules.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import net.diibadaaba.zipdiff.Differences;
import org.apache.maven.model.Dependency;
import org.junit.Test;

public class ErrorTest {

    @Test
    public void tostring() throws Exception {
        Error error = new Error(new Dependency(), "bla", new Differences());
        assertThat(error.toString().contains("'Total differences: 0}"), is(true));
    }

}