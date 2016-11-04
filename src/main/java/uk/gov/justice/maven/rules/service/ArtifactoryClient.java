package uk.gov.justice.maven.rules.service;

import static java.lang.String.format;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.logging.Log;

public class ArtifactoryClient {

    private static final int SOCKET_TIMEOUT = 5000;
    private static final int CONNECTION_TIMEOUT = 3000;

    static final String REQUEST_PATH = "/artifactory/api/search/versions";
    private static final String REQUEST_QUERY_PARAMS = "?g=%s&a=%s&repos=libs-release-local";
    private static final String REQUEST = REQUEST_PATH + REQUEST_QUERY_PARAMS;


    private static final int NO_PROXY = -1;

    private String artifactoryUrl;
    private String proxyHost;
    private int proxyPort = NO_PROXY;

    private Log log;

    public ArtifactoryClient(String artifactoryUrl, String proxyHost, int proxyPort, Log log) {
        this.artifactoryUrl = artifactoryUrl;
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        this.log = log;
    }

    public ArtifactoryClient(String artifactoryUrl, Log log) {
        this.artifactoryUrl = artifactoryUrl;
        this.log = log;
    }

    public String findArtifactInfo(Dependency ramlDependency) throws IOException {
        String payload;

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpHost httpHost = HttpHost.create(artifactoryUrl.replace("/artifactory", ""));

            HttpHost target = new HttpHost(httpHost.getHostName(), httpHost.getPort(), "http");

            RequestConfig.Builder requestConfigBuilder = RequestConfig.custom()
                    .setSocketTimeout(SOCKET_TIMEOUT)
                    .setConnectTimeout(CONNECTION_TIMEOUT);

            if (proxyPort != -1) {
                requestConfigBuilder.setProxy(new HttpHost(proxyHost, proxyPort, "http"));
                log.debug("configured proxy: " + proxyHost);
            }

            RequestConfig config = requestConfigBuilder.build();
            HttpGet request = new HttpGet(format(REQUEST, ramlDependency.getGroupId(), ramlDependency.getArtifactId()));
            request.setConfig(config);

            log.debug("Sending request for " + request.getRequestLine() + " to " + target + " via proxy " + proxyHost + ":" + proxyPort);

            try (CloseableHttpResponse response = httpClient.execute(target, request);) {
                log.debug("response code:" + response.getStatusLine());
                payload = EntityUtils.toString(response.getEntity());
                log.debug("payload:" + payload);
            }
        }

        return payload;
    }

}
