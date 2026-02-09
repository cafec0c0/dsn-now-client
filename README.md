# Deep Space Now (DSN) Java Client

This library provides a client for interacting with the API that powers the 
[DSN Now](https://eyes.nasa.gov/apps/dsn-now/dsn.html) webpage.

## Usage

### Adding dsn-now-client to your project
First, add the package as a dependency in your project:

**Maven**:
```xml
 <dependency>
  <groupId>net.adambruce</groupId>
  <artifactId>dsn-now-client</artifactId>
  <version>${version}</version>
</dependency> 
```

**Gradle**:
```groovy
implementation("net.adambruce:dsn-now-client:${version}")
```

### Using the Client
To use the client, create an instance of the client and call one of the following methods:


#### Fetching the DSN Now Configuration
The DSN Now configuration contains basic information about DSN's ground stations, dishes and tracked spacecraft.

```java
DeepSpaceNetworkClient client = DeepSpaceNetworkClient.newDeepSpaceNetworkClient();
Configuration configuration = client.fetchConfiguration();
```

#### Fetching the current DSN state
The DSN Now state contains up-to-date state information about ground stations, dishes, spacecraft and communications.

```java
DeepSpaceNetworkClient client = DeepSpaceNetworkClient.newDeepSpaceNetworkClient();
State state = client.fetchState();
```

#### Fetching the current DSN state and merging it with the DSN configuration
Combines the above two queries to create a single coherent model hierarchy.

```java
DeepSpaceNetworkClient client = DeepSpaceNetworkClient.newDeepSpaceNetworkClient();
MergedData data = client.fetchMergedData();
```

## Building from Source
To build from source, you will require:
- Java 8+ 
- Maven

Run the following command (use -Prelease to generate source JAR):
```shell
mvn clean install
```