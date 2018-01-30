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

import org.jbehavesupport.runner.story.AndStepStories
import org.junit.runner.Description
import org.junit.runner.notification.RunNotifier
import spock.lang.Specification
import spock.util.environment.RestoreSystemProperties
/**
 * @author Michal Bocek
 * @since 29/01/18
 */
class AndStepStoriesTest extends Specification {

    def notifier = Mock(RunNotifier)

    def "Test correct notifications"() {
        given:
        def runner = new JUnitRunner(AndStepStories)

        when:
        runner.run(notifier)

        then:
        1 * notifier.fireTestStarted({it.displayName.startsWith("BeforeStories")} as Description)
        then:
        1 * notifier.fireTestFinished({it.displayName.startsWith("BeforeStories")} as Description)
        then:
        1 * notifier.fireTestStarted({it.displayName.equals("Story: AndStep")} as Description)
        then:
        1 * notifier.fireTestStarted({it.displayName.equals("Scenario: Scenario with and step")} as Description)
        then:
        1 * notifier.fireTestStarted({it.displayName.contains("Given say Hello")} as Description)
        then:
        1 * notifier.fireTestFinished({it.displayName.contains("Given say Hello")} as Description)
        then:
        1 * notifier.fireTestStarted({it.displayName.contains("And say Hello")} as Description)
        then:
        1 * notifier.fireTestFinished({it.displayName.contains("And say Hello")} as Description)
        then:
        1 * notifier.fireTestStarted({it.displayName.contains("And say Hello")} as Description)
        then:
        1 * notifier.fireTestFinished({it.displayName.contains("And say Hello")} as Description)
        then:
        1 * notifier.fireTestStarted({it.displayName.contains("And say Hello")} as Description)
        then:
        1 * notifier.fireTestFinished({it.displayName.contains("And say Hello")} as Description)
        then:
        1 * notifier.fireTestStarted({it.displayName.contains("Given say Hello")} as Description)
        then:
        1 * notifier.fireTestFinished({it.displayName.contains("Given say Hello")} as Description)
        then:
        1 * notifier.fireTestStarted({it.displayName.contains("And say Hello")} as Description)
        then:
        1 * notifier.fireTestFinished({it.displayName.contains("And say Hello")} as Description)
        then:
        1 * notifier.fireTestStarted({it.displayName.contains("Given say Hello")} as Description)
        then:
        1 * notifier.fireTestFinished({it.displayName.contains("Given say Hello")} as Description)
        then:
        1 * notifier.fireTestFinished({it.displayName.equals("Scenario: Scenario with and step")} as Description)
        then:
        1 * notifier.fireTestFinished({it.displayName.equals("Story: AndStep")} as Description)
        then:
        1 * notifier.fireTestStarted({it.displayName.startsWith("AfterStories")} as Description)
        then:
        1 * notifier.fireTestFinished({it.displayName.startsWith("AfterStories")} as Description)
    }

    def "Test descriptions"() {
        given:
        def runner = new JUnitRunner(AndStepStories)

        when:
        def desc = runner.description
        def children = desc.children

        then:
        desc.testClass == AndStepStories
        children.size() == 3
        children[0].displayName =~ /BeforeStories.*/
        children[1].displayName == "Story: AndStep"
        children[1].children[0].displayName == "Scenario: Scenario with and step"
        children[1].children[0].children[0].displayName =~ /Given say Hello.*\)/
        children[1].children[0].children[1].displayName =~ /And say Hello.*\)/
        children[1].children[0].children[2].displayName =~ /And say Hello.*\)/
        children[1].children[0].children[3].displayName =~ /And say Hello.*\)/
        children[1].children[0].children[4].displayName =~ /Given say Hello.*\)/
        children[1].children[0].children[5].displayName =~ /And say Hello.*\)/
        children[1].children[0].children[6].displayName =~ /Given say Hello.*\)/
        children[2].displayName =~ /AfterStories.*/
    }


    @RestoreSystemProperties
    def "Test correct notifications for story level reporter"() {
        given:
        System.setProperty("jbehave.report.level", "STORY")
        def runner = new JUnitRunner(AndStepStories)

        when:
        runner.run(notifier)

        then:
        1 * notifier.fireTestStarted({it.displayName.startsWith("Story: AndStep")} as Description)
        then:
        1 * notifier.fireTestFinished({it.displayName.startsWith("Story: AndStep")} as Description)
    }

    @RestoreSystemProperties
    def "Test descriptions for story level reporter"() {
        given:
        System.setProperty("jbehave.report.level", "STORY")
        def runner = new JUnitRunner(AndStepStories)

        when:
        def desc = runner.description
        def children = desc.children

        then:
        desc.testClass == AndStepStories
        children.size() == 1
        children[0].displayName =~ /Story: AndStep.*/
        children[0].children.size() == 0
    }
}
