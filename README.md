# esmf-sdk Demo

Demonstrates various combinations of using the [esmf-sdk](https://github.com/eclipse-esmf/esmf-sdk) to access Aspect Model information and Aspect data.

Demo code and generation of artifacts uses
the [PartAsPlanned 2.0.0](https://github.com/eclipse-tractusx/sldt-semantic-models/blob/main/io.catenax.part_as_planned/2.0.0/PartAsPlanned.ttl)
Aspect Model as defined by the Tractus-X project.

To run all generation steps and tests, run `mvn clean test`.

## Generation of artifacts using the Maven Plugin

In this project, the pom.xml is configured to use the esmf-aspect-model-maven-plugin to generate:

* Java classes (POJOs)
* Static Java classes
* OpenAPI specification
* JSON Schema

Additionally, the project uses the openapi-generator project with the java-wiremock configuration to generate
a mockserver implementation for the Aspect's OpenAPI specification.
All generated code is located in the `src-gen` folder.

## Demo of Java APIs

In the class `EsmfSdkTest` some functionalities of the Java API are demonstrated:

* Load an Aspect Model from File, InputStream or directly via URN
* Generate JSON sample payload for the Aspect, then use Jackson to parse it to create an instance of the
  corresponding generated Java POJO
* Programmatically generate HTML documentation for an Aspect Model
* Usage of the generated static Java classes to navigate the Aspect Model in a type-safe way
* Usage of static Java classes to access deserialized data

In the class `PartAsPlannedApiMockTest`, the generated mockserver implementation is used.

* The mockserver is started for the Aspect's API
* A HTTP request to the mock endpoint is done
* Result is displayed

