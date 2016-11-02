package uk.gov.justice.maven.rules.service;

import static com.google.common.io.Resources.getResource;
import static java.nio.charset.Charset.defaultCharset;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;

import uk.gov.justice.maven.rules.domain.Artifact;
import uk.gov.justice.maven.rules.domain.ArtifactoryInfo;

import com.google.common.io.Resources;
import org.apache.maven.plugin.logging.Log;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ArtifactInfoParserTest {

    @Mock
    Log log;

    @Test
    public void parse() throws Exception {
        String json = Resources.toString(getResource("fixtures/artifact-version-2.0.188.json"), defaultCharset());

        ArtifactoryInfo artifactoryInfo = new ArtifactoryParser(log).parse(json);

        assertThat(artifactoryInfo.getResults(), containsInAnyOrder(new Artifact("2.0.187"), new Artifact("2.0.188")));
    }

    @Test
    public void parseError() throws Exception {
        String json = Resources.toString(getResource("fixtures/artifactory-response-error.json"), defaultCharset());

        ArtifactoryInfo artifactoryInfo = new ArtifactoryParser(log).parse(json);

        assertThat(artifactoryInfo.getResults().isEmpty(), is(true));
        verify(log).error(anyString());
    }

}