package uk.gov.justice.maven.rules;


import uk.gov.justice.maven.rules.domain.RuleException;
import uk.gov.justice.maven.rules.service.ArtifactComparator;
import uk.gov.justice.maven.rules.service.ArtifactUrlBuilder;
import uk.gov.justice.maven.rules.service.ArtifactoryClient;
import uk.gov.justice.maven.rules.service.ArtifactoryParser;
import uk.gov.justice.maven.rules.service.RequireLatestVersionsService;

import org.apache.maven.enforcer.rule.api.EnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;

public class RequireLatestVersionsRule
        implements EnforcerRule {

    private String filter;


    //todo add tests
    public void execute(EnforcerRuleHelper helper) throws EnforcerRuleException {
        try {
            MavenProject mavenProject = (MavenProject) helper.evaluate("${project}");
            Settings settings = (Settings) helper.evaluate("${settings}");

            Proxy proxy = (Proxy) settings.getProxies().get(0);
            String artifactoryUrl = getArtifactoryUrl(mavenProject);

            ArtifactoryClient artifactoryClient = new ArtifactoryClient(new ArtifactUrlBuilder(), artifactoryUrl, proxy.getHost(), proxy.getPort(), helper.getLog());
            ArtifactoryParser artifactoryParser = new ArtifactoryParser(helper.getLog());
            ArtifactComparator artifactComparator = new ArtifactComparator(helper.getLog(), filter);

            RequireLatestVersionsService requireLatestVersionsService = new RequireLatestVersionsService(artifactoryClient, artifactoryParser,
                    mavenProject, helper.getLog(), artifactComparator);

            requireLatestVersionsService.execute();
        } catch (RuleException e) {
            throw new EnforcerRuleException(e.getMessage() + e.getError());
        } catch (ExpressionEvaluationException e) {
            e.printStackTrace();
        }
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    private String getArtifactoryUrl(MavenProject mavenProject) {
        return mavenProject.getProperties().get("artifactory.dist.url").toString().replace("/artifactory", "");
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

}
