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

/**
 * @author Michal Bocek
 * @since 29/08/16
 */
public class JUnitRunnerFormatter {

    public static final String FORMAT_SIMPLE = "%s %s";
    public static final String FORMAT_SEMICOLON = "%s: %s";

    private JUnitRunnerFormatter() {
        throw new UnsupportedOperationException();
    }

    public static String buildStoryText(String text) {
        return formatWithSemicolon("Story", text);
    }

    public static String buildScenarioText(Keywords keywords, String text) {
        return formatWithoutSemicolon(keywords.scenario(), text);
    }

    public static String buildExampleText(Keywords keywords, String text) {
        return formatWithoutSemicolon(keywords.examplesTableRow(), text);
    }

    public static String normalizeStoryName(String storyName) {
        String result;
        if (storyName.contains(".")) {
            result = storyName.substring(0, storyName.indexOf("."));
        } else {
            result = storyName;
        }
        return result;
    }

    private static String formatWithoutSemicolon(String prefix, String text) {
        return String.format(FORMAT_SIMPLE, prefix, stripDots(text));
    }

    private static String formatWithSemicolon(String prefix, String text) {
        return String.format(FORMAT_SEMICOLON, prefix, stripDots(text));
    }

    private static String stripDots(String text) {
        return text.replaceAll("\\.", "");
    }
}
