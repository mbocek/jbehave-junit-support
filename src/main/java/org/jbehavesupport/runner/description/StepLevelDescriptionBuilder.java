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

package org.jbehavesupport.runner.description;

import static java.util.Objects.nonNull;
import static org.jbehavesupport.runner.JUnitRunnerFormatter.buildExampleText;
import static org.jbehavesupport.runner.JUnitRunnerFormatter.buildScenarioText;
import static org.jbehavesupport.runner.JUnitRunnerFormatter.buildStoryText;
import static org.jbehavesupport.runner.JUnitRunnerFormatter.normalizeStep;
import static org.jbehavesupport.runner.JUnitRunnerFormatter.normalizeStoryName;
import static org.junit.runner.Description.createSuiteDescription;
import static org.junit.runner.Description.createTestDescription;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.jbehave.core.embedder.PerformableTree;
import org.jbehave.core.model.GivenStory;
import org.jbehave.core.model.Scenario;
import org.jbehave.core.model.Story;
import org.jbehave.core.steps.StepCandidate;
import org.jbehave.core.steps.StepType;
import org.junit.runner.Description;

/**
 * @author Michal Bocek
 * @since 4/23/2017
 */
class StepLevelDescriptionBuilder extends AbstractDescriptionBuilder {

    private UniqueDescriptionGenerator descriptions;
    private String previousNonAndStep;

    public StepLevelDescriptionBuilder(final PerformableTree story) {
        super(story);
        descriptions = new UniqueDescriptionGenerator();
    }

    @Override
    public StoryResult buildDescription() {
        List<Description> descriptions = getStory().getRoot()
            .getStories()
            .stream()
            .map(this::createStoryDescription)
            .collect(Collectors.toList());
        descriptions.add(0, createTestDescription(Story.class, STORIES_BEFORE));
        descriptions.add(createTestDescription(Story.class, STORIES_AFTER));
        return new StoryResult(descriptions);
    }

    protected Description createStoryDescription(PerformableTree.PerformableStory performableStory) {
        String storyString = buildStoryText(performableStory.getStory().getName());
        Description description = createSuiteDescription(descriptions.getUnique(storyString));
        if (hasGivenStories(performableStory)) {
            addGivenStories(description, performableStory.getStory());
        }
        performableStory.getScenarios().forEach(
            performableScenario -> getScenarioDescription(performableScenario).forEach(description::addChild)
        );
        return description;
    }

    private List<Description> getScenarioDescription(PerformableTree.PerformableScenario performableScenario) {
        Description scenarioDescription = createSuiteDescription(buildScenarioText(getKeywords(), performableScenario.getScenario().getTitle()));
        if (performableScenario.hasExamples()) {
            performableScenario.getExamples()
                .stream()
                .map(examplePerformableScenario -> {
                    String exampleString = buildExampleText(getKeywords(), examplePerformableScenario.getParameters().toString());
                    Description exampleDescription = createSuiteDescription(descriptions.getUnique(exampleString));
                    performableScenario.getScenario()
                        .getSteps()
                        .forEach(step -> addIfNotAComment(exampleDescription, step));
                    return exampleDescription;
                })
                .forEach(scenarioDescription::addChild);
        } else {
            if (hasGivenStories(performableScenario)) {
                addGivenStories(scenarioDescription, performableScenario.getScenario());
            }
            performableScenario.getScenario()
                .getSteps()
                .forEach(step -> addIfNotAComment(scenarioDescription, step));
        }
        return Collections.singletonList(scenarioDescription);
    }

    private void addIfNotAComment(Description description, String step) {
        if (isNotAComment(step)) {
            description.addChild(getStepDescription(step));
        }
    }

    private void addGivenStories(Description scenarioDescription, Scenario scenario) {
        scenario.getGivenStories()
            .getStories()
            .forEach(story -> {
                String storyString = normalizeStoryName(story.getPath());
                scenarioDescription.addChild(createTestDescription(GivenStory.class, descriptions.getUnique(storyString)));
            });
    }


    private void addGivenStories(Description storyDescription, Story story) {
        story.getGivenStories()
            .getStories()
            .forEach(givenStory -> {
                String storyString = normalizeStoryName(givenStory.getPath());
                storyDescription.addChild(createTestDescription(GivenStory.class, descriptions.getUnique(storyString)));
            });
    }

    private boolean hasGivenStories(PerformableTree.PerformableScenario performableScenario) {
        return !performableScenario.getScenario().getGivenStories().getPaths().isEmpty();
    }

    private boolean hasGivenStories(PerformableTree.PerformableStory performableStory) {
        return !performableStory.getStory().getGivenStories().getPaths().isEmpty();
    }

    private Description getStepDescription(String step) {
        Description result;
        StepCandidate stepCandidate = findCandidateStep(step);
        if (stepCandidate != null) {
            result = getStepDescription(stepCandidate, step);
        } else {
            result = getStepDescription(UnknownStep.class, step);
        }
        return result;
    }

    private Description getStepDescription(Class<?> stepClass, String step) {
        return createTestDescription(stepClass, descriptions.getUnique(normalizeStep(step)));
    }

    private Description getStepDescription(StepCandidate stepCandidate, String step) {
        Description result;
        String uniqueStep = descriptions.getUnique(normalizeStep(step));
        if (stepCandidate.isComposite()) {
            result = createSuiteDescription(uniqueStep);
            Arrays.stream(stepCandidate.composedSteps())
                .forEach(childStep -> addIfNotAComment(result, childStep));
        } else {
            result = createTestDescription(stepCandidate.getStepsType(), uniqueStep);
        }
        return result;
    }

    private StepCandidate findCandidateStep(String step) {
        StepCandidate resultStepCandidate = getStepCandidates().stream()
            .filter(stepCandidate -> stepCandidate.matches(step, previousNonAndStep))
            .findFirst()
            .orElse(null);
        if (nonNull(resultStepCandidate) && resultStepCandidate.getStepType() != StepType.AND) {
            previousNonAndStep = resultStepCandidate.getStartingWord() + " ";
        }
        return resultStepCandidate;
    }

    private boolean isNotAComment(final String stringStepOneLine) {
        boolean result;
        if (getStepCandidates().isEmpty()) {
            result = true;
        } else {
            result = !getStepCandidates().get(0).comment(stringStepOneLine);
        }
        return result;
    }
}
