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
package org.jbehavesupport.runner

import org.jbehavesupport.runner.story.FailInGivenStories
import org.junit.runner.Description
import org.junit.runner.notification.Failure
import org.junit.runner.notification.RunNotifier
import spock.lang.Specification
import spock.util.environment.RestoreSystemProperties

/**
 * @author Michal Bocek
 * @since 28/01/18
 */
class FailInGivenStoryTest extends Specification {

    def notifier = Mock(RunNotifier)

    def "Test correct notifications"() {
        given:
        def runner = new JUnitRunner(FailInGivenStories)

        when:
        runner.run(notifier)

        then:
        1 * notifier.fireTestStarted({it.displayName.startsWith("BeforeStories")} as Description)
        then:
        1 * notifier.fireTestFinished({it.displayName.startsWith("BeforeStories")} as Description)
        then:
        1 * notifier.fireTestStarted({it.displayName.equals("Story: FailInGivenStory")} as Description)
        then:
        1 * notifier.fireTestStarted({it.displayName.equals("Scenario: Given story test")} as Description)
        then:
        1 * notifier.fireTestStarted({it.displayName.contains("FailInGivenStory")} as Description)
        then:
        1 * notifier.fireTestFailure({it.description.displayName.contains("FailInGivenStory")} as Failure)
        then:
        1 * notifier.fireTestFinished({it.displayName.contains("FailInGivenStory")} as Description)
        then:
        1 * notifier.fireTestStarted({it.displayName.contains("GivenStory2")} as Description)
        then:
        1 * notifier.fireTestFinished({it.displayName.contains("GivenStory2")} as Description)
        then:
        1 * notifier.fireTestStarted({it.displayName.contains("Then User with name Tester is properly signed in")} as Description)
        then:
        1 * notifier.fireTestFinished({it.displayName.contains("Then User with name Tester is properly signed in")} as Description)
        then:
        1 * notifier.fireTestFinished({it.displayName.equals("Scenario: Given story test")} as Description)
        then:
        1 * notifier.fireTestFinished({it.displayName.equals("Story: FailInGivenStory")} as Description)
        then:
        1 * notifier.fireTestStarted({it.displayName.startsWith("AfterStories")} as Description)
        then:
        1 * notifier.fireTestFinished({it.displayName.startsWith("AfterStories")} as Description)
    }

    def "Test descriptions"() {
        given:
        def runner = new JUnitRunner(FailInGivenStories)

        when:
        def desc = runner.description
        def children = desc.children

        then:
        desc.testClass == FailInGivenStories
        children.size() == 3
        children[0].displayName =~ /BeforeStories.*/
        children[1].displayName == "Story: FailInGivenStory"
        children[1].children[0].displayName == "Scenario: Given story test"
        children[1].children[0].children[0].displayName =~ /.*FailInGivenStory/
        children[1].children[0].children[1].displayName =~ /.*GivenStory2/
        children[1].children[0].children[2].displayName =~ /Then User with name Tester is properly signed in.*/
        children[2].displayName =~ /AfterStories.*/
    }

    @RestoreSystemProperties
    def "Test correct notifications for story level reporter"() {
        given:
        System.setProperty("jbehave.report.level", "STORY")
        def runner = new JUnitRunner(FailInGivenStories)

        when:
        runner.run(notifier)

        then:
        1 * notifier.fireTestStarted({it.displayName.startsWith("Story: FailInGivenStory")} as Description)
        then:
        1 * notifier.fireTestFailure({it.description.displayName.contains("Story: FailInGivenStory")} as Failure)
        then:
        1 * notifier.fireTestFinished({it.displayName.startsWith("Story: FailInGivenStory")} as Description)
    }

    @RestoreSystemProperties
    def "Test descriptions for story level reporter"() {
        given:
        System.setProperty("jbehave.report.level", "STORY")
        def runner = new JUnitRunner(FailInGivenStories)

        when:
        def desc = runner.description
        def children = desc.children

        then:
        desc.testClass == FailInGivenStories
        children.size() == 1
        children[0].displayName =~ /Story: FailInGivenStory.*/
        children[0].children.size() == 0
    }
}
