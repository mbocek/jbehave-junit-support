# Maven setup

In https://oss.sonatype.org you can get the token and simply use in server ossr profile. 
```xml
  <servers>
    <server>
      <id>ossrh</id>
      <username>${token.username}</username>
      <password>${token.password}</password>
    </server>
  </servers>

  <profiles>
    <profile>
      <id>ossrh</id>
      <activation>
        <activeByDefault>true</activeByDefault>
      </activation>
      <properties>
        <gpg.executable>gpg</gpg.executable>
        <gpg.passphrase>${gpg password}</gpg.passphrase>
      </properties>
    </profile>
  </profiles>
```

Then you can simply run the command below and snapshot are going to be deployed to ossr sonatype repository. 
```bash
mvn clean deploy -Prelease
```

Release is performed by two commands
```bash
mvn release:clean release:prepare -Prelease
mvn release:perform -Prelease
```
