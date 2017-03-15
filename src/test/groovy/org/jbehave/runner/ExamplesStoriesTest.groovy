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

import org.jbehave.runner.story.ExamplesStories
import org.junit.runner.Description
import org.junit.runner.notification.RunNotifier
import spock.lang.Shared
import spock.lang.Specification
/**
 * @author Michal Bocek
 * @since 13/03/17
 */
class ExamplesStoriesTest extends Specification {

    @Shared
    def runner = new JUnitRunner(ExamplesStories)
    def notifier = Mock(RunNotifier)

    def "Test correct notifications"() {
        when:
        runner.run(notifier)

        then:
        1 * notifier.fireTestStarted({((Description)it).displayName.startsWith("BeforeStories")})
        then:
        1 * notifier.fireTestFinished({((Description)it).displayName.startsWith("BeforeStories")})
        then:
        1 * notifier.fireTestStarted({((Description)it).displayName.equals("Story: Examples")})
        then:
        1 * notifier.fireTestStarted({((Description)it).displayName.equals("Scenario: login to system")})
        then:
        1 * notifier.fireTestStarted({((Description)it).displayName.contains("Example: {url=http://examplescom/login, status=OK}")})
        then:
        1 * notifier.fireTestStarted({((Description)it).displayName.contains("Given login with data")})
        then:
        1 * notifier.fireTestFinished({((Description)it).displayName.contains("Given login with data")})
        then:
        1 * notifier.fireTestStarted({((Description)it).displayName.contains("When I submit login data")})
        then:
        1 * notifier.fireTestFinished({((Description)it).displayName.contains("When I submit login data")})
        then:
        1 * notifier.fireTestStarted({((Description)it).displayName.contains("Then user should be logged in")})
        then:
        1 * notifier.fireTestFinished({((Description)it).displayName.contains("Then user should be logged in")})
        then:
        1 * notifier.fireTestFinished({((Description)it).displayName.contains("Example: {url=http://examplescom/login, status=OK}")})
        then:
        1 * notifier.fireTestStarted({((Description)it).displayName.contains("Example: {url=http://examplescom/logout, status=NOK}")})
        then:
        1 * notifier.fireTestStarted({((Description)it).displayName.contains("Given login with data")})
        then:
        1 * notifier.fireTestFinished({((Description)it).displayName.contains("Given login with data")})
        then:
        1 * notifier.fireTestStarted({((Description)it).displayName.contains("When I submit login data")})
        then:
        1 * notifier.fireTestFinished({((Description)it).displayName.contains("When I submit login data")})
        then:
        1 * notifier.fireTestStarted({((Description)it).displayName.contains("Then user should be logged in")})
        then:
        1 * notifier.fireTestFinished({((Description)it).displayName.contains("Then user should be logged in")})
        then:
        1 * notifier.fireTestFinished({((Description)it).displayName.contains("Example: {url=http://examplescom/logout, status=NOK}")})
        then:
        1 * notifier.fireTestFinished({((Description)it).displayName.equals("Scenario: login to system")})
        then:
        1 * notifier.fireTestFinished({((Description)it).displayName.equals("Story: Examples")})
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
        desc.testClass == ExamplesStories
        children.size() == 3
        children[0].displayName =~ /BeforeStories.*/
        children[1].displayName == "Story: Examples"
        children[1].children[0].displayName == "Scenario: login to system"
        children[1].children[0].children[0].displayName =~ /Example.*/
        children[1].children[0].children[0].children[0].displayName =~ /Given login with data.*/
        children[1].children[0].children[0].children[1].displayName =~ /When I submit login data on.*/
        children[1].children[0].children[0].children[2].displayName =~ /Then user should be logged in.*/
        children[1].children[0].children[1].displayName =~ /Example.*/
        children[1].children[0].children[1].children[0].displayName =~ /Given login with data.*/
        children[1].children[0].children[1].children[1].displayName =~ /When I submit login data on.*/
        children[1].children[0].children[1].children[2].displayName =~ /Then user should be logged in.*/
        children[2].displayName =~ /AfterStories.*/
    }
}
