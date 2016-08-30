/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jbehave.runner;

import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.model.Story;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

import java.util.Iterator;

import static org.jbehave.runner.JUnitRunnerFormatter.buildStoryText;
import static org.jbehave.runner.JUnitRunnerFormatter.normalizeStoryName;

/**
 * @author Michal Bocek
 * @since 29/08/16
 */
public class JUnitStepReporter extends LoggingReporter {

    public static final String BEFORE_STORIES = "BeforeStories";
    private static final String AFTER_STORIES = "AfterStories";

    private final RunNotifier notifier;
    private final int testCount;
    private final Description rootDesctiprion;
    private final Configuration configuration;

    private int executedSteps;
    private Description currentDescription;
    private Iterator<Description> scenariosDescription;
    private Description currentScenarioDescription;
    private Iterator<Description> stepsDescription;
    private Description currentStepDescription;

    public JUnitStepReporter(RunNotifier notifier, int testCount, Description rootDescription,
                             Configuration configuration) {
        this.notifier = notifier;
        this.testCount = testCount;
        this.rootDesctiprion = rootDescription;
        this.configuration = configuration;
    }

    @Override
    public void beforeStory(Story story, boolean givenStory) {
        for (Description description : rootDesctiprion.getChildren()) {
            if (description.isTest()
                && (isEligibleAs(story, description, BEFORE_STORIES)
                    || isEligibleAs(story, description, AFTER_STORIES))) {
                currentDescription = description;
                notifier.fireTestStarted(currentDescription);
                executedSteps++;

            }
            if (description.isSuite()
                && isEligibleAs(description, story.getName())) {
                currentDescription = description;
                notifier.fireTestStarted(currentDescription);
                scenariosDescription = currentDescription.getChildren().iterator();
            }
        }
        super.beforeStory(story, givenStory);
    }

    @Override
    public void afterStory(boolean givenOrRestartingStory) {
        super.afterStory(givenOrRestartingStory);
        if (currentDescription != null) {
            notifier.fireTestFinished(currentDescription);
        }
    }

    @Override
    public void beforeScenario(String scenarioTitle) {
        currentScenarioDescription = scenariosDescription.next();
        stepsDescription = currentScenarioDescription.getChildren().iterator();
        notifier.fireTestStarted(currentScenarioDescription);
        super.beforeScenario(scenarioTitle);
    }

    @Override
    public void afterScenario() {
        notifier.fireTestFinished(currentScenarioDescription);
        super.afterScenario();
    }

    @Override
    public void beforeStep(String step) {
        currentStepDescription = stepsDescription.next();
        notifier.fireTestStarted(currentStepDescription);
        super.beforeStep(step);
    }

    @Override
    public void successful(String step) {
        super.successful(step);
        executedSteps++;
        notifier.fireTestFinished(currentStepDescription);
    }

    private boolean isEligibleAs(Story story, Description description, String storyName) {
        return story.getName().equals(storyName)
            && isEligibleAs(description, storyName);
    }

    private boolean isEligibleAs(Description description, String storyName) {
        return description.getDisplayName().equals(buildStoryText(normalizeStoryName(storyName)));
    }
}
