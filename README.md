# README #

This README would normally document whatever steps are necessary to get your application up and running.

### How to build and run the sample app ###

First we need to get the git project 

```
git clone git@bitbucket.org:SolentechLab/com.solentech.node.sensor.git sensor
cd sensor
```

Build the package 

```
mvn package
```

You will see `sensor-*.jar` file in `/target`.

To run the application in terminal simply use the below command:

```
java -jar target/sensor-1.0.jar <Sensor IP address>
```

Have fun!
