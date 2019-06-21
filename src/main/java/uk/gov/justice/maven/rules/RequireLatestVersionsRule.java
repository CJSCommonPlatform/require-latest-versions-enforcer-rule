package uk.gov.justice.maven.rules;


import static java.lang.String.format;

import uk.gov.justice.maven.rules.service.ArtifactComparator;
import uk.gov.justice.maven.rules.service.ArtifactComparatorFactory;
import uk.gov.justice.maven.rules.service.ArtifactFinder;
import uk.gov.justice.maven.rules.service.ArtifactFinderFactory;
import uk.gov.justice.maven.rules.service.ArtifactVersionCheckingService;
import uk.gov.justice.maven.rules.service.CheckablePlugins;
import uk.gov.justice.maven.rules.service.RuleException;

import org.apache.maven.enforcer.rule.api.EnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

public class RequireLatestVersionsRule implements EnforcerRule {

    private ArtifactVersionCheckingService artifactVersionCheckingService = new ArtifactVersionCheckingService(new CheckablePlugins());
    private ArtifactFinderFactory artifactFinderFactory = new ArtifactFinderFactory();
    private ArtifactComparatorFactory artifactComparatorFactory = new ArtifactComparatorFactory();
    private String filter;

    @Override
    public void execute(final EnforcerRuleHelper helper) throws EnforcerRuleException {

        try {

            final MavenProject mavenProject = (MavenProject) helper.evaluate("${project}");
            final Log log = helper.getLog();
            final ArtifactFinder artifactFinder = artifactFinderFactory.artifactFinderFrom(helper);
            final ArtifactComparator artifactComparator = artifactComparatorFactory.artifactComparatorOf(filter, log);

            artifactVersionCheckingService.checkVersionMismatches(
                    mavenProject,
                    artifactFinder,
                    artifactComparator,
                    log);

        } catch (final RuleException e) {
            throw new EnforcerRuleException(format("%s%s", e.getMessage(), e.getError()));
        } catch (final ExpressionEvaluationException | ComponentLookupException e) {
            throw new EnforcerRuleException(e.getMessage());
        }
    }

    public void setFilter(final String filter) {
        this.filter = filter;
    }

    @Override
    public boolean isCacheable() {
        return false;
    }

    @Override
    public boolean isResultValid(EnforcerRule arg0) {
        return false;
    }

    @Override
    public String getCacheId() {
        return "";
    }
}