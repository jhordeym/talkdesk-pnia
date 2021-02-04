# Talkdesk code challenge: Phone number information aggregator

## Introduction:

Design and implement an API that, given a list of phone numbers, aggregates phone information by sector of activity for each existing phone prefix. 

To achieve that, a single REST endpoint was created for the AggregatorController:

Endpoint:

```
    local: POST `http://localhost:8888/aggregate`

    docker: POST `http://localhost:8080/aggregate`
```

Payload-schema:

```
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "type": "array",
  "items": {
    "type": "string"
  }
}
```

Response-schema:

```
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "type": "object",
  "additionalProperties": {
    "type": "object",
    "additionalProperties": {
      "type": "number"
    }
  }
}
```


Example of payload:

```
["+1983248", "001382355", "+147 8192", "+4439877"]
```

Example of response:

```
{
    "1": {
        "Clothing": 1,
        "Technology": 2
    },
    "44": {
        "Banking": 1
    }
}
```

    
More details about the API can be found at (Swagger UI):

```
    local: POST `http://localhost:8888/swagger-ui.html`

    docker: POST `http://localhost:8080/swagger-ui.html`
```

    

## Requirements:

- Validation of phone number:
```
com.talkdesk.pnia.util.PhoneNumberUtils.java
```
- Read prefixes from file ("prefixes.txt"):
```
com.talkdesk.pnia.service.PrefixReader.java
```
- Fetch business sector data from existing API:
```
com.talkdesk.pnia.webclient.BusinessSectorAPI.java
```

## Architecture choice:

#### Back-end:
    - Java 8
    - Spring Boot 2
    - Spring Cloud
        - OpenFeign
    - REST API
    - Project Lombok
    - Swagger UI
    - Maven

#### Deployment:
    - Docker 

## Explanation:

The solution consists in a single micro-service developed with Spring boot 2.4.2, using the spring-boot-starter-parent parent.

#### pnia:
    
    description:
        The application is responsible to recieve external REST API calls (POST);
        connect to the business sector API using FeignClient, in order to retrieve the sector of activity information for each given mobile number.
        Then, read all prefixes from a given file ("prefixes.txt") and aggregates for each prefix, 
        the amount of phones associated to each existing sector, returning a JSON REST response in case of Success with HttpStatus 200.

    
    dependencies:
        - spring boot starter web:
            Provides a set of classes in order to create REST controllers.
        - open-feign:
            Used to connect to web-services in a simple way.
        - swagger/swagger-ui:
            Provides an web interface documentation for the implemented REST API.
        - lombok:
            Annotation processor that makes our lives easier :)

## Profiles


There are 2 main profiles: dev and docker which can be used with the flag "-P dev" or "-P docker"

A third profile is used for integration testing: "-P integrationtest"

## Testing

There is both Unit testing and Integration testing implemented.

The first can be found at: 
```
PhoneNumberUtilsTest.java
```
In order to validate if the Regex parser used to comply with the provided rules for a given phone number, this class was implemented.
The following set of examples was used:

```
static {
        expectedMap = new HashMap<>();
        expectedMap.put("00123", true); // min digits with 00
        expectedMap.put("+123", true); // min digits with +
        expectedMap.put("+123A", false); // wrong case because A
        expectedMap.put("+1234", false); // invalid range 4 / {3} or {6,13}
        expectedMap.put("+ 123", false); // invalid space
        expectedMap.put("+123456", true); // min digits of 2nd range {6,13}
        expectedMap.put("+123 456", true); // min digits of 2nd range with spaces
        expectedMap.put("+1234567890123", true); // max digits of 2nd range {6,13}
        expectedMap.put("+123 45 67 89 01 23", true); // max digits of 2nd range with spaces
    }
```

In order to update the test cases, it's only needed to add or remove a key, value from this Map and re-run the test.

For integration testing:
```
PniaApplicationTests.java
```

This class consists in an integration test for both the reading of the resources:
```
@Test
void prefixReaderIntegrationTest() throws AggregatorServiceException
```

as long as testing the implemented controller:
```
@Test
void givenPhoneNumbers_whenAggregate_thenStatus200() throws Exception
```
## Results timing

Running the request in the following environments these were the results:

http://localhost:8888/aggregate (dev):
```
curl -d '["+1983236248", "+1 7490276403", "001382355A", "+351917382672", "+35191734022"]' "http://localhost:8888/aggregate" -H "Content-Type: application/json" -H "Accept: application/json" -w "\n%{time_total} secs\n"
{"1":{"Technology":1,"Banking":1},"3519173":{"Clothing":2}}
2,274348 secs
```

http://localhost:8080/aggregate (docker):
```
~ curl -d '["+1983236248", "+1 7490276403", "001382355A", "+351917382672", "+35191734022"]' "http://localhost:8080/aggregate" -H "Content-Type: application/json" -H "Accept: application/json" -w "\n%{time_total} secs\n"
{"1":{"Technology":1,"Banking":1},"3519173":{"Clothing":2}}
2,717550 secs
```

https://talkdesk-pnia.herokuapp.com/aggregate (heroku):
```
~ curl -d '["+1983236248", "+1 7490276403", "001382355A", "+351917382672", "+35191734022"]' "https://talkdesk-pnia.herokuapp.com/aggregate" -H "Content-Type: application/json" -H "Accept: application/json" -w "\n%{time_total} secs\n"
{"1":{"Technology":1,"Banking":1},"3519173":{"Clothing":2}}
8,951640 secs
```

With the provided "validate.sh" script:
```
local:

./validate.sh localhost:8888/aggregate
./validate.sh: line 27: jq: command not found
./validate.sh: line 29: jq: command not found
SUCCESS the API complies with the spec


docker:

./validate.sh localhost:8080/aggregate
./validate.sh: line 27: jq: command not found
./validate.sh: line 29: jq: command not found
SUCCESS the API complies with the spec


heroku:

./validate.sh https://talkdesk-pnia.herokuapp.com/aggregate
FAIL the API could not be reached, returned an error, or did not reply within 5 seconds
```

As we can see, for both local and docker response time of API is around 2 secs 
(which is within the limits of the validate.sh script), so the choice for Deployment is Docker.

## Build

To build everything:
    
Run the following script `build4docker.sh` at root dir.

This script will perform the maven installation, create a docker image and run the images in a container.

For this you will need:
    
[Java JDK 8](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

[Apache Maven](https://maven.apache.org/download.cgi) (Or use the provided "mvnw" executable in the code)

[Docker](https://www.docker.com/)
