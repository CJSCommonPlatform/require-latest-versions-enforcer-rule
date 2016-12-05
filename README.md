# Require Latest Versions

A Maven Enforcer rule for ensuring that any project using the _raml-maven-plugin_ depending on the
latest released version of any RAML dependencies. This is done by comparing version numbers and
if the version is out of date, whether there are any modifications in the RAML or JSON schema files.

Custom filename filters using regular expressions can be used to control which file modifications
are considered to be differences.

## Usage

Add the plugin to the build plugins section of any project which is using _raml-maven-plugin_:


```
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-enforcer-plugin</artifactId>
    <dependencies>
        <dependency>
            <groupId>uk.gov.justice.maven</groupId>
            <artifactId>require-latest-versions-enforcer-rule</artifactId>
            <version>1.0.3-SNAPSHOT</version>
        </dependency>
    </dependencies>
    <executions>
        <execution>
            <id>enforce</id>
            <configuration>
                <rules>
                    <RequireLatestVersionsRule implementation="uk.gov.justice.maven.rules.RequireLatestVersionsRule">
                        <filter>^raml/json/schema/?|.*raml$</filter>                                        
                    </RequireLatestVersionsRule>
                </rules>
            </configuration>
            <goals>
                <goal>enforce</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
