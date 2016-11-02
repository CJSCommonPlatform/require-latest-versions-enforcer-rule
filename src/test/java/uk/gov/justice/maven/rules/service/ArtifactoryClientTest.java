package uk.gov.justice.maven.rules.service;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.givenThat;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.google.common.io.Resources.getResource;
import static java.nio.charset.Charset.defaultCharset;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static uk.gov.justice.maven.rules.service.RequireLatestVersionsService.RAML;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.io.Resources;
import com.jayway.restassured.path.json.JsonPath;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.logging.Log;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ArtifactoryClientTest {

    private static final int ARTIFACTORY_PORT = 8081;
    private static final String ARTIFACTORY_URL = "localhost";
    private static final int PROXY_PORT = -1;
    private static final String PROXY_HOST = "";
    private static final String GROUP_A = "group-a";
    private static final String ARTIFACT_B = "artifact-b";

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8081);

    @Mock
    private Log log;

    private ArtifactoryClient artifactoryClient;

    @Before
    public void setUp() throws Exception {
        artifactoryClient = new ArtifactoryClient(ARTIFACTORY_URL, ARTIFACTORY_PORT, log);
    }

    @Test
    public void findArtifactInfo() throws Exception {
        String json = Resources.toString(getResource("fixtures/artifact-version-2.0.188.json"), defaultCharset());

        givenThat(get(urlEqualTo("/artifactory/api/search/versions?g=" + GROUP_A + "&a=" + ARTIFACT_B + "&repos=libs-release-local"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(json)
                        .withHeader("Content-Type", "application/json")));

        String artifactInfo = artifactoryClient.findArtifactInfo(dependency(GROUP_A, ARTIFACT_B));

        assertThat(new JsonPath(artifactInfo).getString("results"),
                is("[[integration:false, version:2.0.188], [integration:false, version:2.0.187]]"));
    }

    private Dependency dependency(String group, String artifact) {
        Dependency ramlDep = new Dependency();
        ramlDep.setClassifier(RAML);
        ramlDep.setGroupId(group);
        ramlDep.setArtifactId(artifact);
        ramlDep.setVersion("1");
        return ramlDep;
    }

}