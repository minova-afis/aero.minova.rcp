# aero.minova.rcp

Der Standard RCP Fatclient von MINOVA


Die Dokumentation ist im [Wiki](https://github.com/minova-afis/aero.minova.rcp/wiki) dieses Projekts zu finden.

Änderungen können im [Changelog](https://github.com/minova-afis/aero.minova.rcp/blob/master/Changelog.md) nachvollzogen werden.

## Sonar Execution from command line

* `main` branch
  
``` 
mvn clean install dependency-check:aggregate sonar:sonar -Dsonar.projectKey=minova-afis_aero.minova.rcp -Dsonar.branch.name=main
```

* `main` branch w/o OWASP dependency check
```
mvn clean install sonar:sonar -Dsonar.projectKey=minova-afis_aero.minova.rcp -Dsonar.branch.name=main
```

