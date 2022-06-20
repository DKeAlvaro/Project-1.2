# Configuration Reference

The configuration is read as java properties file (sectionless
.ini-style, if you wish), with key-value pairs separated by equals sign,
for example:

```
prefix.option = value
```

Settings listed below that don't have default values are optional.

## Course options

| Key                             | Aliases       | Default | Description                             |
|:--------------------------------|:--------------|:--------|:----------------------------------------|
| course.width                    |               | 50      | Course width, in meters                 |
| course.height                   |               | 50      | Course height, in meters                |
| course.surface                  | heightProfile |         | Course surface function                 |
| course.friction.default.static  | mus           | 0.2     | Ground static friction coefficient      |
| course.friction.default.kinetic | muk           | 0.1     | Ground kinetic friction coefficient     |
| course.friction.sand.static     | muss          | 0.3     | Sand static friction coefficient        |
| course.friction.sand.kinetic    | muks          | 0.25    | Sand kinetic friction coefficient       |
| course.target.x                 | xt            |         | Position of the hole, x-axis component  |
| course.target.y                 | yt            |         | Position of the hole, y-axis component  |
| course.target.radius            | r             | 0.1     | Radius of the hole                      |
| course.ball.position.x          | x0            | 0       | Initial ball position, x-axis component |
| course.ball.position.y          | y0            | 0       | Initial ball position, y-axis component |
| course.ball.velocity.x          | vx            | 0       | Initial ball velocity, x-axis component |
| course.ball.velocity.y          | vy            | 0       | Initial ball velocity, y-axis component |

### Obstacles

All obstacles in this project are treated by engine as cylinders or
circular surfaces.

`[identifier]` may be any arbitrary string, including numbers.

| Key                                               | Aliases       | Default | Description                                                                            |
|:--------------------------------------------------|:--------------|:--------|:---------------------------------------------------------------------------------------|
| course.items.trees.instances.[identifier].x       |               |         | Tree center x-axis component                                                           |
| course.items.trees.instances..[identifier].y      |               |         | Tree center y-axis component                                                           |
| course.items.sandpits.instances.[identifier].x    |               |         | Sandpit center x-axis component                                                        |
| course.items.sandpits.instances.[identifier].y    |               |         | Sandpit center y-axis component                                                        |
| course.items.lakes.instances.[identifier].start.x | startingLakeX |         | Lake top-left x-axis component, to be deprecated in favor of center + radius style     |
| course.items.lakes.instances.[identifier].start.y | endingLakeX   |         | Lake top-left y-axis component, to be deprecated in favor of center + radius style     |
| course.items.lakes.instances.[identifier].end.x   | startingLakeY |         | Lake bottom-right x-axis component, to be deprecated in favor of center + radius style |
| course.items.lakes.instances.[identifier].end.y   | endingLakeY   |         | Lake bottom-right y-axis component, to be deprecated in favor of center + radius style |


## Engine options

| Key                                  | Aliases | Default | Description                                                                                                                                                                               |
|:-------------------------------------|:--------|:--------|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| engine.physics.acceleration          |         | basic   | Physics implementation to use. Possible options are "basic" and "advanced".                                                                                                               |
| engine.physics.ode-solver            |         | euler   | ODE solver selection. When used with GUI, the latter will overwrite this value. Possible options are "euler", "rk2" and "rk4".                                                            |
| engine.noise.velocity                |         |         | Maximum noise added to or subtracted from hit velocity, as a fraction of actual velocity                                                                                                  |
| engine.noise.direction               |         |         | Maximum noise added to or subtracted from hit direction, as a fraction of pi (so 1.0 would allow full circle from -pi to + pi)                                                            |
| engine.noise.value                   |         |         | Allows to set both of the above as one value, acts as fallback (i.e. if one of the above is not set, engine will look for this value)                                                     |
| engine.timing.step                   |         | 0.01    | Simulated timestep between two calculations, in seconds.                                                                                                                                  |
| engine.timing.intervals.computation  |         |         | Time interval between two calculations, in seconds. By default, when omitted, it is calculated as {step} seconds.                                                                         |
| engine.timing.intervals.notification |         | 0.016   | Time interval between updates to GUI, in seconds. Please note that GUI updates are linked to computations and only done as end of computation operation, thus this number is approximate. |
