# Require Latest Versions

A Maven Enforcer rule for ensuring that raml-maven-plugin dependencies are using the latest released version which has any API modifications
in the raml or schema file comparing with current version of the dependency.

Please note you can set custom file names modifications of which you are interested in by setting by filter using regexp.

Usage

Add the plugin to the build plugins section of any project which is using raml-maven-plugin: 

            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-enforcer-plugin</artifactId>
                <dependencies>
                    <dependency>
                        <groupId>uk.gov.justice.maven</groupId>
                        <artifactId>require-latest-versions-enforcer-rule</artifactId>
                        <version>1.0.0-SNAPSHOT</version>
                    </dependency>
                </dependencies>
                <executions>
                    <execution>
                        <id>enforce</id>
                        <configuration>
                            <rules>
                                <RequireLatestVersionsRule
                                        implementation="uk.gov.justice.maven.rules.RequireLatestVersionsRule">
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