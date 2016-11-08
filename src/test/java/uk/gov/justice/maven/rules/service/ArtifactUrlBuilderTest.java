package uk.gov.justice.maven.rules.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.apache.maven.model.Dependency;
import org.junit.Test;

public class ArtifactUrlBuilderTest {

    @Test
    public void testBuild() throws Exception {

        Dependency dependency = new Dependency();
        dependency.setGroupId("com.fasterxml.jackson.core");
        dependency.setArtifactId("jackson-core");
        dependency.setVersion("1.2");

        String url = new ArtifactUrlBuilder().build(dependency);

        assertThat(url, is("com/fasterxml/jackson/core/jackson-core/1.2/jackson-core-1.2-raml.jar"));
    }

}