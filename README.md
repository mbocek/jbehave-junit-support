# JUnit support for JBehave
[![Build Status](https://travis-ci.org/jbehavesupport/jbehave-junit-support.svg?branch=master)](https://travis-ci.org/jbehavesupport/jbehave-junit-support)
[![Coverage Status](https://coveralls.io/repos/github/jbehavesupport/jbehave-junit-support/badge.svg?branch=master)](https://coveralls.io/github/jbehavesupport/jbehave-junit-support?branch=master)
[![codebeat badge](https://codebeat.co/badges/adbb13e0-c146-4f58-847b-c1db713efbb7)](https://codebeat.co/projects/github-com-jbehavesupport-jbehave-junit-support-master)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/c8ccfdab225040f1ae25e128fab9ba4b)](https://www.codacy.com/app/mbocek/jbehave-junit-support?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=jbehavesupport/jbehave-junit-support&amp;utm_campaign=Badge_Grade)

Support library which should help with running jbahave BDD tests. 

## Integration to java project
Setup in the maven project:
```xml
<dependency>
    <groupId>org.jbehavesupport</groupId>
    <artifactId>jbehave-junit-support</artifactId>
</dependency>
```

Very simple java class with runner implementation:
```java
@RunWith(JUnitRunner.class)
public class BasicStory extends JUnitStory {

    public BasicStory() {
        JUnitRunnerConfiguration.recommendedConfiguration(configuredEmbedder());
    }

    @Override
    public Configuration configuration() {
        return new MostUsefulConfiguration();
    }

    ...
}
```
