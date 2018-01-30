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

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jbehave.core.configuration.Keywords;
import org.jbehave.core.embedder.PerformableTree;
import org.jbehave.core.steps.CandidateSteps;
import org.jbehave.core.steps.StepCandidate;
import org.junit.runner.Description;

/**
 * @author Michal Bocek
 * @since 4/23/2017
 */
@RequiredArgsConstructor
public abstract class AbstractDescriptionBuilder implements DescriptionBuilder {

    public static final String STORIES_BEFORE = "BeforeStories";
    public static final String STORIES_AFTER= "AfterStories";

    @Getter
    private final List<StepCandidate> stepCandidates = new ArrayList<>();

    @Getter
    private Keywords keywords = new Keywords();

    @Getter
    private final PerformableTree story;

    @Override
    public DescriptionBuilder withCandidateSteps(List<CandidateSteps> candidateSteps) {
        for (CandidateSteps candidateStep : candidateSteps) {
            stepCandidates.addAll(candidateStep.listCandidates());
        }
        return this;
    }

    @Override
    public DescriptionBuilder withKeywords(Keywords keywords) {
        this.keywords = keywords;
        return this;
    }

    protected abstract Description createStoryDescription(PerformableTree.PerformableStory performableStory);
}

