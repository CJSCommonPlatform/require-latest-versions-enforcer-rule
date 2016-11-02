package uk.gov.justice.maven.rules;


import uk.gov.justice.maven.rules.domain.RuleException;
import uk.gov.justice.maven.rules.service.ApiConvergenceService;
import uk.gov.justice.maven.rules.service.ArtifactoryClient;
import uk.gov.justice.maven.rules.service.ArtifactoryParser;

import org.apache.maven.enforcer.rule.api.EnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;

public class ApiConvergenceRule
        implements EnforcerRule {

    private String artifactoryUrl;
    private int artifactoryPort;
    private String proxyHost;
    private int proxyPort;

    public void execute(EnforcerRuleHelper helper) throws EnforcerRuleException {
        try {
            new ApiConvergenceService(new ArtifactoryClient(artifactoryUrl, artifactoryPort, proxyHost, proxyPort, helper.getLog()), new ArtifactoryParser(helper.getLog()), helper).execute();
        } catch (RuleException e) {
            throw new EnforcerRuleException(e.getMessage() + e.getError());
        }
    }

    public String getCacheId() {
        return "";
    }

    public boolean isCacheable() {
        return false;
    }

    public boolean isResultValid(EnforcerRule arg0) {
        return false;
    }

    public void setArtifactoryUrl(String artifactoryUrl) {
        this.artifactoryUrl = artifactoryUrl;
    }

    public void setArtifactoryPort(int artifactoryPort) {
        this.artifactoryPort = artifactoryPort;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }
}
