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

import org.jbehave.core.model.*;
import org.jbehave.core.reporters.StoryReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * @author Michal Bocek
 * @since 29/08/16
 */
public class LoggingReporter implements StoryReporter {
    private static Logger logger = LoggerFactory.getLogger(LoggingReporter.class);

    @Override
    public void storyNotAllowed(Story story, String filter) {
        logger.info("Story: {} not allowed for filter: {}", story.getName(), filter);
    }

    @Override
    public void storyCancelled(Story story, StoryDuration storyDuration) {
        logger.info("Story: {} cancelled in: {}s", story.getName(), storyDuration.getDurationInSecs());
    }

    @Override
    public void beforeStory(Story story, boolean givenStory) {
        logger.info("Before story: {}", story.getName() + (givenStory ? "(given story)" : ""));
    }

    @Override
    public void afterStory(boolean givenOrRestartingStory) {
        logger.info("After story");
    }

    @Override
    public void narrative(Narrative narrative) {
        if (!narrative.isEmpty()) {
            logger.info("Narrative:");
        }
        if (!narrative.inOrderTo().isEmpty()) {
            logger.info("In order to {}", narrative.inOrderTo());
        }
        if (!narrative.asA().isEmpty()) {
            logger.info("As a {}", narrative.asA());
        }
        if (!narrative.iWantTo().isEmpty()) {
            logger.info("I want to {}", narrative.iWantTo());
        }
        if (!narrative.soThat().isEmpty()) {
            logger.info("So that {}", narrative.soThat());
        }
    }

    @Override
    public void lifecyle(Lifecycle lifecycle) {
        if (!lifecycle.isEmpty()) {
            logger.info("Lifecycle: {}", lifecycle);
        }
    }

    @Override
    public void scenarioNotAllowed(Scenario scenario, String filter) {
        logger.info("Scenario: {} not allowed by filer: {}", scenario.getTitle(), filter);
    }

    @Override
    public void beforeScenario(String scenarioTitle) {
        logger.info("Before scenario: {}", scenarioTitle);
    }

    @Override
    public void scenarioMeta(Meta meta) {
        logger.info("Scenario meta: {}", meta);
    }

    @Override
    public void afterScenario() {
        logger.info("After scenario");
    }

    @Override
    public void givenStories(GivenStories givenStories) {
        logger.info("Given stories: {}", givenStories);
    }

    @Override
    public void givenStories(List<String> storyPaths) {
        logger.info("Given stories: {}", storyPaths);
    }

    @Override
    public void beforeExamples(List<String> steps, ExamplesTable table) {
        logger.info("Before steps: {} with example table: {}", steps, table);
    }

    @Override
    public void example(Map<String, String> tableRow) {
        logger.info("Example: {}", tableRow);
    }

    @Override
    public void afterExamples() {
        logger.info("After examles");
    }

    @Override
    public void beforeStep(String step) {
        logger.info("Before step: {}", step);
    }

    @Override
    public void successful(String step) {
        logger.info("Successful step: {}", step);
    }

    @Override
    public void ignorable(String step) {
        logger.info("Ignorable step: {}", step);
    }

    @Override
    public void pending(String step) {
        logger.info("Pending step: {}", step);
    }

    @Override
    public void notPerformed(String step) {
        logger.warn("Not performed step: {}", step);
    }

    @Override
    public void failed(String step, Throwable cause) {
        logger.error("Failed step: {} cause: {}", step, cause);
    }

    @Override
    public void failedOutcomes(String step, OutcomesTable table) {
        logger.error("Faild step: {} outcomes: {}", step, table);
    }

    @Override
    public void restarted(String step, Throwable cause) {
        logger.info("Restarted step: {} because of: {}", step, cause);
    }

    @Override
    public void restartedStory(Story story, Throwable cause) {
        logger.error("Restarted story: {} because of: {}", story.getName(), cause);
    }

    @Override
    public void dryRun() {
        logger.info("Dry run");
    }

    @Override
    public void pendingMethods(List<String> methods) {
        logger.info("Pending methods: {}", methods);
    }
}
