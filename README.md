# Project 1-2 Putting - Group 19

This repository contains source code for group 19 collaboration on UM 
DKE 2022 semester project 1-2. The goal of the project is creation of
a simplified putting game simulator, including simplified physics 
modeling using numerical function approximation, 3d representation, 
interface for human player, AI bot capable of winning the game and 
more details of lesser importance. 

The project schedule contains three phases (iterations). First phase 
implies creation of the physics engine using the most basic 
approximation (basic Euler method for finding function value using its
derivative and previous value), simple visualization and human player
interface. Second phase implies complete visualization, advanced physics 
modeling methods and a couple of AI implementations capable of winning 
the game. Third and last phase implies introduction of obstacles and 
creating an AI that would be able to win the game in a maze-like 
environment

## Group 19 - Team Members

- [Zijian Dong](https://github.com/zijiandongkurt)
- Ana Godorogea
- [Julia Grassot](https://github.com/juliagrst)
- [Álvaro Menéndez Ros](https://github.com/DKeAlvaro)
- [Anna Nowowiejska](https://github.com/annanowo)
- Agata Oskroba
- [Arseny Trifonov](https://github.com/etki)

## Software

### Organization

All source code is organized in `src` directory.

`bin` directory contains additional scripts.

### Building

The project is built by [maven](https://maven.apache.org/). Maven 
[wrapper](https://maven.apache.org/wrapper/) is also used, so target 
machine doesn't need to have anything but JDK installed.

To build the project, simply invoke `./mvnw package` 
(`./mvnw.cmd package` on Windows may be required). This will compile the 
project and create an executable JAR file in `target/` 
(`target/putting-1.0.0-SNAPSHOT.jar`).

### Launching

The built JAR contains all definitions for launch:

```
java -jar target/putting-1.0.0-SNAPSHOT.jar
```

JAR supports two arguments:

- Path to configuration file with surface function, friction values,
and so on. By default, it looks for configuration.properties.
- Path to configuration file with hits in following format:

    ```
    x = 10, y = -10
    x = 1.0, y = 2
    x = 3.5, y = 6.5
    ```

Alternatively, use maven:

```
./mvnw java:exec
```
