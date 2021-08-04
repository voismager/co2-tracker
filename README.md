# CO2 tracker

A simple project dedicated to collect data from hundreds of thousands 
of CO2 sensors and alert if the CO2 concentrations reach critical levels. 

## Structure

The project consist of the following components:

* [`rest-service`] - an REST API service for collecting sensor measurements
and retrieving sensor statuses and metrics
* [`InfluxDB`] - a time series database used to store measurements

[`rest-service`]: rest-service
[`InfluxDB`]: https://docs.influxdata.com/influxdb/v2.0/

## Manually building and running services

To build Docker image of rest-service and use it locally, use the following command:

    ./gradlew jibDockerBuild

This command will build images for all the services and save them to your local Docker installation, so you can inspect or run the image as any other local container. (You need to have Docker installed locally).

After building Docker images using JIB, use docker-compose to launch services. 
You can propagate your own config file (or just use the default one) as follows:

    docker-compose --env-file .env up

In order to launch unit and integration tests, launch the following Gradle tasks:

    ./gradlew test
    ./gradlew integrationTest
    
Tests don't require any additional setup and should work as it is.