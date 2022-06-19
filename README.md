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
- [Ana Godorogea](https://github.com/AnaGodorogea)
- [Julia Grassot](https://github.com/juliagrst)
- [Álvaro Menéndez Ros](https://github.com/DKeAlvaro)
- [Anna Nowowiejska](https://github.com/annanowo)
- [Agata Oskroba](https://github.com/agata-oskroba)
- [Arseny Trifonov](https://github.com/etki)

## Software

### Organization

Source code is stored in `src` directory using classic java conventions
(`main/java` for source code, `main/resources` for assets, `test/java`
for tests). Additional documentation about the inner working and design 
choices is placed in `doc`.

### Setup Guide

1. Install Java 17 or above & IDEA (seriously, vscode won't suffice).
2. Import the project in your IDEA.
3. Set Java SDK to installed one in File -> Project Structure.
4. Open gradle tab on the right and double-click Tasks -> other -> dist.
If it successfully finishes, then it means your build environment is 
properly set.
5. Open gradle tab on the right and double-click Tasks -> documentation 
-> javadoc. After completion, right-click on `doc/generated` -> Mark 
directory as -> Excluded.
6. Copy `configuration.properties.dist` to `configuration.properties`.
The difference between them is that first goes into git, the latter does
not.
7. If IDEA complaints about something that can't be found after git 
pull, check for gradle update icon in right corner and click it. If icon
is missing, try doing it manually in gradle tab -> right-click on the 
project -> reload gradle project.

### Building

The project is built by [gradle](https://gradle.org/).

To build the project, simply invoke `./gradlew dist`. This will compile 
the project and create an executable JAR file 
`build/libs/project12.group19.putting-1.0.0.jar`.

### Launching

The built JAR contains all definitions for launch:

```
java -jar build/libs/project12.group19.putting-1.0.0.jar
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

The configuration file (`configuration.properties`) is mandatory, but 
one can be obtained just by copying `configuration.properties.dist` in
the project directory.

After the launch GUI will be presented, where someone has to select
solver and bot through the menu. For unknown reasons on Windows 
machines the menu is not displayed, so following selection has to be 
made:

- Press 1, 2 or 3 for Euler, RK2 or RK4 solvers accordingly
- Press R for rule-based or B for hill-climbing bot
- Press P to launch the configuration

## Configuration

The configuration is read as java properties file (sectionless 
.ini-style, if you wish), with key-value pairs separated by equals sign,
for example:

```
prefix.option = value
```

Full reference is available in [doc/configuration.md](). Most used 
options are:

| Key                                               | Aliases       | Default | Description                                                                            |
|:--------------------------------------------------|:--------------|:--------|:---------------------------------------------------------------------------------------|
| course.surface                                    | heightProfile |         | Course surface function                                                                |
| course.friction.default.static                    | mus           | 0.2     | Ground static friction coefficient                                                     |
| course.friction.default.kinetic                   | muk           | 0.1     | Ground kinetic friction coefficient                                                    |
| course.friction.sand.static                       | muss          | 0.3     | Sand static friction coefficient                                                       |
| course.friction.sand.kinetic                      | muks          | 0.25    | Sand kinetic friction coefficient                                                      |
| course.target.x                                   | xt            |         | Position of the hole, x-axis component                                                 |
| course.target.y                                   | yt            |         | Position of the hole, y-axis component                                                 |
| course.target.radius                              | r             | 0.1     | Radius of the hole                                                                     |
| course.ball.position.x                            | x0            | 0       | Initial ball position, x-axis component                                                |
| course.ball.position.y                            | y0            | 0       | Initial ball position, y-axis component                                                |
| engine.timing.step                                |               | 0.01    | Simulated timestep between two calculations, in seconds.                               |
| course.items.sandpits.instances.[identifier].x    |               |         | Sandpit center x-axis component                                                        |
| course.items.sandpits.instances.[identifier].y    |               |         | Sandpit center y-axis component                                                        |
| course.items.lakes.instances.[identifier].start.x | startingLakeX |         | Lake top-left x-axis component, to be deprecated in favor of center + radius style     |
| course.items.lakes.instances.[identifier].start.y | endingLakeX   |         | Lake top-left y-axis component, to be deprecated in favor of center + radius style     |
| course.items.lakes.instances.[identifier].end.x   | startingLakeY |         | Lake bottom-right x-axis component, to be deprecated in favor of center + radius style |
| course.items.lakes.instances.[identifier].end.y   | endingLakeY   |         | Lake bottom-right y-axis component, to be deprecated in favor of center + radius style |
