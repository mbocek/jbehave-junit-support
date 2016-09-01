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

import org.jbehave.core.configuration.Keywords;
import org.jbehave.core.model.Scenario;
import org.jbehave.core.model.Story;
import org.jbehave.core.steps.CandidateSteps;
import org.jbehave.core.steps.StepCandidate;
import org.junit.runner.Description;

import java.util.ArrayList;
import java.util.List;

import static org.jbehave.runner.JUnitRunnerFormatter.buildStoryText;
import static org.jbehave.runner.JUnitRunnerFormatter.normalizeStoryName;

/**
 * @author Michal Bocek
 * @since 27/08/16
 */
public class StoryParser {

    private Story story;

    private StoryParser(Story story) {
        this.story = story;
    }

    public Story getStory() {
        return story;
    }

    public static StoryDescription parse(Story story) {
        return new StoryDescription(new StoryParser(story));
    }

    public static class StoryResult {
        private final Description storyDescription;
        private final int testCount;

        public StoryResult(Description storyDescription, int testCount) {
            this.storyDescription = storyDescription;
            this.testCount = testCount;
        }

        public Description getStoryDescription() {
            return storyDescription;
        }

        public int getTestCount() {
            return testCount;
        }
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
            Description description = createDescription(storyParser.getStory());
            return new StoryResult(description, testCount);
        }

        private Description createDescription(Story story) {
            Description description = Description.createSuiteDescription(
                buildStoryText(normalizeStoryName(story.getName())));
            for (Scenario scenario : story.getScenarios()) {
                description.addChild(getScenarioDescription(scenario));
            }
            return description;
        }

        private Description getScenarioDescription(Scenario scenario) {
            Description scenarioDescription = Description
                .createSuiteDescription(JUnitRunnerFormatter.buildScenarioText(keywords, scenario.getTitle()));
            for (String step : scenario.getSteps()) {
                Description description = getStepDescription(step);
                scenarioDescription.addChild(description);
            }
            return scenarioDescription;
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
            return Description.createTestDescription(stepClass, step);
        }

        private Description getStepDescription(StepCandidate stepCandidate, String step) {
            Description result;
            if (stepCandidate.isComposite()) {
                result = Description.createSuiteDescription(step);
                String[] childSteps = stepCandidate.composedSteps();
                for (String childStep : childSteps) {
                    result.addChild(getStepDescription(childStep));
                }
            } else {
                testCount++;
                result = Description.createTestDescription(stepCandidate.getStepsType(), step);
            }
            return result;
        }

        private StepCandidate findCandidateStep(String step) {
            StepCandidate result = null;
            for (StepCandidate stepCandidate : stepCandidates) {
                if (stepCandidate.matches(step)) {
                    result = stepCandidate;
                }
            }
            return result;
        }
    }

    private static class UnknownStep {
        private UnknownStep() {
            throw new UnsupportedOperationException();
        }
    }
}
