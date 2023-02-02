Existence of the `src` directory prevents issue below from happening, if executing Maven with `sonar:sonar` argument. 
```
[ERROR] Failed to execute goal org.sonarsource.scanner.maven:sonar-maven-plugin:3.9.1.2184:sonar (default-cli) on project aero.minova.rcp.root: The directory '/Users/scholz/IdeaProjects/aero.minova.rcp/report-aggregate/src' does not exist for Maven module aero.minova.rcp:aero.minova.rcp.report-aggregate:jar:12.7.3-SNAPSHOT. Please check the property sonar.tests -> [Help 1]
[ERROR] 
[ERROR] To see the full stack trace of the errors, re-run Maven with the -e switch.
[ERROR] Re-run Maven using the -X switch to enable full debug logging.
[ERROR] 
[ERROR] For more information about the errors and possible solutions, please read the following articles:
[ERROR] [Help 1] http://cwiki.apache.org/confluence/display/MAVEN/MojoExecutionException
```