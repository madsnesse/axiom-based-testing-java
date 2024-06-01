[![Maven Package](https://github.com/madsnesse/axiom-based-testing-java/actions/workflows/maven-publish.yml/badge.svg?branch=main)](https://github.com/madsnesse/axiom-based-testing-java/actions/workflows/maven-publish.yml)

## Prerequisites
* JDK 17
* Maven (3.6.3)


## Installation
* Clone the repository
* Run `mvn clean install` in the repository folder
* Start adding axioms, for an example project, See [Position example](https://github.com/madsnesse/PositionExample)

## Adding maven dependency
* To add the dependency without cloning the repository do the following
* Follow GitHubs instructions for [authenticating to GitHub Packages](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry#authenticating-to-github-packages)
  * Change OWNER/REPOSITORY to madsnesse/axiom-based-testing-java
* Set up you `pom.xml` file like shown in [Example pom](https://github.com/madsnesse/PositionExample/blob/main/pom.xml)
* Change the <jaxioms.version> to the latest version found in the [releases](https://github.com/madsnesse/axiom-based-testing-java/releases) page