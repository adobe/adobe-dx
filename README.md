# Adobe Dx
[![Quality gate](https://sonarcloud.io/api/project_badges/quality_gate?project=com.adobe.dx%3Areactor)](https://sonarcloud.io/dashboard?id=com.adobe.dx%3Areactor) 

![Build](https://github.com/adobe/adobe-dx/workflows/Build/badge.svg) [![Coverage](https://sonarcloud.io/api/project_badges/measure?project=com.adobe.dx%3Areactor&metric=coverage)](https://sonarcloud.io/dashboard?id=com.adobe.dx%3Areactor)
- dx core [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.adobe.dx/core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.adobe.dx/core)
- dx testing extenstion [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.adobe.dx/testing-extensions/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.adobe.dx/testing-extensions)
- dx admin [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.adobe.dx/admin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.adobe.dx/admin)
- dx structure components [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.adobe.dx/structure/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.adobe.dx/structure)
- dx content components [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.adobe.dx/content/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.adobe.dx/content)


### Description 

A toolkit for AEM to help build exceptional digital experiences, on top of [AEM core components](https://github.com/adobe/aem-core-wcm-components)

#### building DX modules

first ensure you have adobe public repository setup ([see 'adobe-public' profile in our bots settings.xml file](./settings.xml))

DX is a set of modules that can be used separately, you can build them all using
 
```mvn```

command at the root of the project.
`all` package contains all modules. 

you might want to just install docs application, after the build just run
```cd apps/docs && mvn content-package:install```

if you want your build to be directly installed to your local aem instance, just add `install-all` maven profile

```mvn -Pinstall-all``` 

### Contributing

Contributions are welcomed! Read the [Contributing Guide](./.github/CONTRIBUTING.md) for more information.

### Releasing

For authorized people willing to release a module, look at [Release guide](./.github/RELEASING.md) for more information.

### Discussion

For ongoing discussions related to adobe-dx, see [the adobe-dx-dev page](https://github.com/orgs/adobe/teams/adobe-dx-devs).

### Licensing

This project is licensed under the Apache V2 License. See [LICENSE](LICENSE) for more information.

