# Require Latest Versions

A Maven Enforcer rule for ensuring that certain dependencies are using the latest version available

Usage

Add the plugin to the build plugins section and set up artifactory and proxy if required. 

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