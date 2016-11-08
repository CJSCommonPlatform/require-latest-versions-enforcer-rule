package uk.gov.justice.maven.rules.service;

import static java.lang.String.format;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.HttpEntity;
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

    static final String REQUEST_PATH = "/artifactory";
    protected static final int SOCKET_TIMEOUT = 5000;
    protected static final int CONNECTION_TIMEOUT = 3000;

    private static final String REQUEST_QUERY_PARAMS = "/api/search/versions?g=%s&a=%s&repos=libs-release-local";
    private static final String REQUEST = REQUEST_PATH + REQUEST_QUERY_PARAMS;
    private static final String MAVEN_REQUIRE_LATEST_VERSIONS_RULE = "maven_require_latest_versions_rule";
    private ArtifactUrlBuilder artifactUrlBuilder;

    private static final int NO_PROXY = -1;

    private String artifactoryUrl;
    private String proxyHost;
    private int proxyPort = NO_PROXY;

    private Log log;

    public ArtifactoryClient(ArtifactUrlBuilder artifactUrlBuilder, String artifactoryUrl, String proxyHost, int proxyPort, Log log) {
        this.artifactUrlBuilder = artifactUrlBuilder;
        this.artifactoryUrl = artifactoryUrl;
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        this.log = log;
    }

    public ArtifactoryClient(ArtifactUrlBuilder artifactUrlBuilder, String artifactoryUrl, Log log) {
        this.artifactUrlBuilder = artifactUrlBuilder;
        this.artifactoryUrl = artifactoryUrl;
        this.log = log;
    }

    //todo refactor
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

    //todo add tests and refactor
    public File getArtifact(Dependency ramlDependency) throws IOException {
        File file;

        String url = artifactUrlBuilder.build(ramlDependency);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpHost httpHost = HttpHost.create(artifactoryUrl.replace("/artifactory", ""));

            HttpHost target = new HttpHost(httpHost.getHostName(), httpHost.getPort(), "http");

            RequestConfig.Builder requestConfigBuilder = RequestConfig.custom()
                    .setSocketTimeout(SOCKET_TIMEOUT)
                    .setConnectTimeout(CONNECTION_TIMEOUT);

            if (proxyPort != -1) {
                requestConfigBuilder.setProxy(new HttpHost(proxyHost, proxyPort, "http"));
            }

            RequestConfig config = requestConfigBuilder.build();
            HttpGet request = new HttpGet(REQUEST_PATH + "/libs-release-local/" + url);
            request.setConfig(config);


            String fileName = ramlDependency.getArtifactId() + "-" + ramlDependency.getVersion() + "-" + "raml.jar";
            String dir = System.getProperty("java.io.tmpdir") + File.separator + MAVEN_REQUIRE_LATEST_VERSIONS_RULE + File.separator;

            file = new File(dir + File.separator + fileName);
            file.getParentFile().mkdirs();

            log.debug("downloaded file :" + file.getAbsolutePath());

            try (CloseableHttpResponse response = httpClient.execute(target, request)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    try (FileOutputStream outstream = new FileOutputStream(file)) {
                        entity.writeTo(outstream);
                    }
                }
            }
        }
        return file;
    }

}
