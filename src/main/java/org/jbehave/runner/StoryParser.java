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

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jbehave.core.configuration.Keywords;
import org.jbehave.core.embedder.PerformableTree;
import org.jbehave.core.model.Story;
import org.jbehave.core.steps.CandidateSteps;
import org.jbehave.core.steps.StepCandidate;
import org.junit.runner.Description;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.jbehave.runner.JUnitRunnerFormatter.*;
import static org.junit.runner.Description.createSuiteDescription;
import static org.junit.runner.Description.createTestDescription;

/**
 * @author Michal Bocek
 * @since 27/08/16
 */
public class StoryParser {

    private static final String STORIES_BEFORE = "BeforeStories";
    private static final String STORIES_AFTER= "AfterStories";

    private PerformableTree performableTree;

    private StoryParser(PerformableTree performableTree) {
        this.performableTree = performableTree;
    }

    public static StoryDescription parse(PerformableTree story) {
        return new StoryDescription(new StoryParser(story));
    }

    @Getter
    @AllArgsConstructor
    public static class StoryResult {
        private final List<Description> storyDescriptions;
        private final int testCount;
    }

    public static class StoryDescription {
        private final StoryParser storyParser;
        private List<StepCandidate> stepCandidates = new ArrayList<>();
        private Keywords keywords = new Keywords();
        private int testCount;

        private StoryDescription(StoryParser storyParser) {
            this.storyParser = storyParser;
        }

        public StoryDescription withCandidateSteps(List<CandidateSteps> candidateSteps) {
            for (CandidateSteps candidateStep : candidateSteps) {
                stepCandidates.addAll(candidateStep.listCandidates());
            }
            return this;
        }

        public StoryDescription withKeywords(Keywords keywords) {
            this.keywords = keywords;
            return this;
        }

        public StoryResult buildDescription() {
            List<PerformableTree.PerformableStory> stories = storyParser.performableTree.getRoot().getStories();
            List<Description> descriptions = stories.stream()
                .map(story -> createStoryDescription(story))
                .collect(Collectors.toList());
            descriptions.add(0, createTestDescription(Story.class, STORIES_BEFORE));
            descriptions.add(createTestDescription(Story.class, STORIES_AFTER));
            return new StoryResult(descriptions, testCount);
        }

        private Description createStoryDescription(PerformableTree.PerformableStory performableStory) {
            Description description = createSuiteDescription(
                buildStoryText(normalizeStoryName(performableStory.getStory().getName())));
            performableStory.getScenarios().forEach(
                performableScenario -> getScenarioDescription(performableScenario).forEach(
                    scenarioDescription -> description.addChild(scenarioDescription))
            );
            return description;
        }

        private List<Description> getScenarioDescription(PerformableTree.PerformableScenario performableScenario) {
            Description scenarioDescription = createSuiteDescription(
                JUnitRunnerFormatter.buildScenarioText(keywords, performableScenario.getScenario().getTitle()));
            if (performableScenario.hasExamples()) {
                performableScenario.getExamples()
                    .stream()
                    .map(examplePerformableScenario -> {
                        Description exampleDescription = createSuiteDescription(
                            buildExampleText(keywords, examplePerformableScenario.getParameters().toString()));
                        performableScenario.getScenario()
                            .getSteps()
                            .forEach(step -> exampleDescription.addChild(getStepDescription(step)));
                        return exampleDescription;
                    })
                    .forEach(exampleDescription -> scenarioDescription.addChild(exampleDescription));
            } else {
                performableScenario.getScenario()
                    .getSteps()
                    .forEach(step -> scenarioDescription.addChild(getStepDescription(step)));
            }
            return Arrays.asList(scenarioDescription);
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
            testCount++;
            return createTestDescription(stepClass, step);
        }

        private Description getStepDescription(StepCandidate stepCandidate, String step) {
            Description result;
            if (stepCandidate.isComposite()) {
                result = createSuiteDescription(step);
                Arrays.stream(stepCandidate.composedSteps())
                    .forEach(childStep -> result.addChild(getStepDescription(childStep)));
            } else {
                testCount++;
                result = createTestDescription(stepCandidate.getStepsType(), step);
            }
            return result;
        }

        private StepCandidate findCandidateStep(String step) {
            return stepCandidates.stream()
                .filter(stepCandidate -> stepCandidate.matches(step))
                .findFirst()
                .orElse(null);
        }
    }

    private static class UnknownStep {
        private UnknownStep() {
            throw new UnsupportedOperationException();
        }
    }
}
