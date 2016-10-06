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
package org.jbehave.runner

import org.jbehave.runner.story.BasicStory
import org.junit.runner.Description
import org.junit.runner.notification.RunNotifier
import spock.lang.Shared
import spock.lang.Specification

/**
 * @author Michal Bocek
 * @since 26/08/16
 */
class BasicStoryTest extends Specification {

    @Shared
    def runner = new JUnitRunner(BasicStory)
    def notifier = Mock(RunNotifier)

    def "Test correct notifications"() {
        when:
        runner.run(notifier)

        then:
        1 * notifier.fireTestStarted({((Description)it).displayName.startsWith("BeforeStories")})
        then:
        1 * notifier.fireTestFinished({((Description)it).displayName.startsWith("BeforeStories")})
        then:
        1 * notifier.fireTestStarted({((Description)it).displayName.equals("Story: basic_story")})
        then:
        1 * notifier.fireTestStarted({((Description)it).displayName.equals("Scenario: Very simple scenario")})
        then:
        1 * notifier.fireTestStarted({((Description)it).displayName.contains("Given say Hello")})
        then:
        1 * notifier.fireTestFinished({((Description)it).displayName.contains("Given say Hello")})
        then:
        1 * notifier.fireTestFinished({((Description)it).displayName.equals("Scenario: Very simple scenario")})
        then:
        1 * notifier.fireTestFinished({((Description)it).displayName.equals("Story: basic_story")})
        then:
        1 * notifier.fireTestStarted({((Description)it).displayName.startsWith("AfterStories")})
        then:
        1 * notifier.fireTestFinished({((Description)it).displayName.startsWith("AfterStories")})
    }

    def "Test descriptions"() {
        when:
        def desc = runner.description
        def children = desc.children

        then:
        desc.testClass == BasicStory
        children.size() == 3
        children[0].displayName =~ /BeforeStories.*/
        children[1].displayName == "Story: basic_story"
        children[1].children[0].displayName == "Scenario: Very simple scenario"
        children[1].children[0].children[0].displayName =~ /Given say Hello\(.*\)/
        children[2].displayName =~ /AfterStories.*/
    }
}
