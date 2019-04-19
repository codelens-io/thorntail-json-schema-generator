[![License](https://img.shields.io/:license-Apache2-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![Build Status](https://travis-ci.org/codelens-io/thorntail-json-schema-generator.svg?branch=master)](https://travis-ci.org/codelens-io/thorntail-json-schema-generator)
[![Coverage Status](https://coveralls.io/repos/github/codelens-io/thorntail-json-schema-generator/badge.svg?branch=master)](https://coveralls.io/github/codelens-io/thorntail-json-schema-generator?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.codelens/thorntail-json-schema-generator/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.codelens/thorntail-json-schema-generator)

# Thorntail (*formerly wildfly-swarm*) configuration schema generator

This utility can be used to generate a **JSON Schema** which can be used to validate
**[Thorntail](https://thorntail.io)** (*[wildfly-swarm](http://wildfly-swarm.io)*) `YAML` configuration files (eg.: `project-defaults.yml`).

The currently generated schema files can be downloaded from [the releases page](https://github.com/codelens-io/thorntail-json-schema-generator/releases)

* thorntail-schema-2.4.0.json
* thorntail-schema-2.4.0-compact.json
* thorntail-schema-2.4.0.properties

## Usage

To generate schema files build the project, and run the jar:

```
$ mvn clean package
$ cd target;
$ java -jar thorntail-json-schema-generator-1.1.1.jar 2.4.0 ~/Developer/thorntail

Thorntail schema generator
============================================================
version: 2.4.0
output directory: /Users/user/Developer/thorntail/
building schema model...
writing schema files...
... done
```

The jar file requires at least one parameter, which is the **version**, the second parameter is the
**output directory**. If omitted, the current directory will be used.

The result `thorntail-schema-<version>-compact.json` can be used in **[IntelliJ IDEA](https://www.jetbrains.com/idea)** to
validate the configuration file, ot the validation can be done with other command line tools,
like: [pajv](https://www.npmjs.com/package/pajv)

```
pajv -s path/to/thorntail-schema-2.4.0.json -d path/to/project-defaults.yml
```

The utility also builds a `properties` file with the available parameters (*with type and documentation*). 

## License

This project is licensed under [Apache 2.0 license]( http://www.apache.org/licenses/LICENSE-2.0).
