package uk.gov.justice.maven.rules.utils;

import static org.hamcrest.MatcherAssert.assertThat;

import uk.gov.justice.maven.rules.domain.Artifact;
import uk.gov.justice.maven.rules.domain.ArtifactoryInfo;
import uk.gov.justice.maven.rules.service.ArtifactUrlBuilder;
import uk.gov.justice.maven.rules.service.ArtifactoryClient;
import uk.gov.justice.maven.rules.service.ArtifactoryParser;

import java.io.IOException;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.hamcrest.core.IsNull;
import org.junit.Ignore;
import org.junit.Test;

public class ExampleUseOfArtifactoryClientTest {
    private static final String ARTIFACTORY_URL = "http://10.124.22.24:8081/artifactory";
    private static final String PROXY_HOST = "10.224.23.8";
    private static final int PROXY_PORT = 3128;

    private static final SystemStreamLog LOG = new SystemStreamLog();

    @Test
    @Ignore(value = "run this manually only to find latest version of artifact")
    public void testClient() throws IOException {

        ArtifactoryClient client = new ArtifactoryClient(new ArtifactUrlBuilder(), ARTIFACTORY_URL, PROXY_HOST, PROXY_PORT, LOG);

        Dependency dependency = new Dependency();
        dependency.setGroupId("uk.gov.moj.cpp.charging");
        dependency.setArtifactId("charging-parent");

        String payload = client.findArtifactInfo(dependency);

        ArtifactoryInfo artifactVersionList = new ArtifactoryParser(LOG).parse(payload);

        artifactVersionList.getResults().sort(Artifact.reverseComparator);

        Artifact lastReleasedArtifactVersion = artifactVersionList.getResults().get(0);
        assertThat(lastReleasedArtifactVersion.getVersion(), IsNull.notNullValue());

        LOG.info(lastReleasedArtifactVersion.getVersion());

    }

}
