# Code Organisation

The project is organised within one module using standard Java class 
naming and location convention, source code is located under 
`/src/main/java` and scarce tests for it are put under `/src/test/java`
using `project12.group19` as the root package.

Several packages are placed within the root package. One of them, `api`,
contains interfaces that are used within the application, while others 
mostly resemble the structure of packages within `api`, providing 
implementations for definitions and additional code: 

- `geometry` contains interfaces for geometric primitives.
- `math` contains types related to math operations, such as 
differential calculations or parsing expressions.
- `domain` contains types related to the field of putting
- `game` contains more specific types related to simulating the 
putting in the boundaries of current project.
- `engine` contains several definitions for the game engine alone, 
without being tied to specific game.
- `ui` contains UI-related definitions.
- `support` and `infrastructure` contain types existing to facilitate 
the application but not related to a specific domain.
- `player` package contains the definitions for a player.

## Common concepts

There are some common concepts implemented throughout the project:

- The interfaces that represent simple structures without much logic are
implemented as records inner to that interface with name `Standard`.
- Interface methods with simple, obvious logic (like calculating 
absolute velocity) are implemented on the interface directly using 
default methods.
- Types that expect common creation patterns, both interfaces with 
standard implementations and complex classes, expose static factory 
methods targeting those patterns.
