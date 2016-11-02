# Require Latest Versions

A Maven Enforcer rule for ensuring that certain dependencies are using the latest version available

Usage

Add the plugin to build plugins and set up artifactory and proxy if required. 

```
       <build>
           <plugins>
               <plugin>
                   <groupId>org.apache.maven.plugins</groupId>
                   <artifactId>maven-enforcer-plugin</artifactId>
                   <version>1.0-SNAPSHOT</version>
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
                                   <ApiConvergenceRule
                                           implementation="uk.gov.justice.maven.rules.ApiConvergenceRule">
                                       <artifactoryUrl>emartifactory.sandboxes.dev2.cloud.local
                                       </artifactoryUrl>
                                       <artifactoryPort>8081</artifactoryPort>
                                       <proxyHost>10.224.23.8</proxyHost>
                                       <proxyPort>3128</proxyPort>
                                   </ApiConvergenceRule>
                               </rules>
                           </configuration>
                           <goals>
                               <goal>enforce</goal>
                           </goals>
                       </execution>
                   </executions>
               </plugin>
           </plugins>
       </build>

```