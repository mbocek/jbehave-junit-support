package org.jbehave.runner.story;

import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.junit.JUnitStories;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.InstanceStepsFactory;
import org.jbehave.runner.JUnitRunner;
import org.jbehave.runner.JUnitRunnerConfiguration;
import org.junit.runner.RunWith;

import java.util.List;

/**
 * @author Michal Bocek
 * @since 16/03/2017
 */
@RunWith(JUnitRunner.class)
public abstract class AbstractStories extends JUnitStories {

    public AbstractStories() {
        JUnitRunnerConfiguration.recommendedConfiguration(configuredEmbedder());
    }

    @Override
    public Configuration configuration() {
        return new MostUsefulConfiguration();
    }

    @Override
    public InjectableStepsFactory stepsFactory() {
        return new InstanceStepsFactory(configuration(), getStepClasses());
    }

    protected abstract List<?> getStepClasses();
}
