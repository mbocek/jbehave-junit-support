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

import static org.jbehavesupport.runner.JUnitRunnerFormatter.buildStoryText;
import static org.junit.runner.Description.createTestDescription;

import java.util.List;
import java.util.stream.Collectors;

import org.jbehave.core.embedder.PerformableTree;
import org.jbehave.core.model.Story;
import org.junit.runner.Description;

/**
 * @author Michal Bocek
 * @since 4/23/2017
 */
class StoryLevelDescriptionBuilder extends AbstractDescriptionBuilder {

    private UniqueDescriptionGenerator descriptions = new UniqueDescriptionGenerator();

    public StoryLevelDescriptionBuilder(final PerformableTree story) {
        super(story);
    }


    @Override
    public StoryResult buildDescription() {
        List<Description> descriptions = getStory().getRoot()
            .getStories()
            .stream()
            .map(this::createStoryDescription)
            .collect(Collectors.toList());
        return new StoryResult(descriptions);
    }

    @Override
    protected Description createStoryDescription(final PerformableTree.PerformableStory story) {
        return createTestDescription(Story.class, buildStoryText(descriptions.getUnique(story.getStory().getName())));
    }
}
