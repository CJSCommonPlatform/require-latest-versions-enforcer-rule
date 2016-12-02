package uk.gov.justice.maven.rules.service;

import java.util.List;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

public class ArtifactFinderFactory {
    public ArtifactFinder artifactFinderFrom(final EnforcerRuleHelper helper) throws ExpressionEvaluationException, ComponentLookupException {
        final ArtifactRepository localRepository = (ArtifactRepository) helper.evaluate("${localRepository}");
        final List<ArtifactRepository> remoteRepositories = (List<ArtifactRepository>) helper.evaluate("${project.remoteArtifactRepositories}");
        final ArtifactResolver resolver = (ArtifactResolver) helper.getComponent(ArtifactResolver.class);

        return new MavenArtifactFinder(resolver, localRepository, remoteRepositories);
    }

}
