# Architecture

This article explains the architecture of the application.

## The engine

The application is built on top of the classic event loop concept. This
application differs a bit from classic implementations, given that every 
recurrent task is expected to *eventually* complete with a result, as 
seen in `EventLoop` interface. `EventLoop` is implemented by 
`ScheduledEventLoop`, which also has one distinct feature - it 
recognizes zero scheduling intervals and runs such tasks at once in a 
single while loop. This would be a bad decision in a bigger engine, 
which expects many tasks to be run at the same tick, but for our 
purposes it allows to save execution time by blocking single thread.

The `EventLoop` is used by `GameHandler`, which utilises the former by 
submitting a game state update task. The task ends when the terminal 
condition is reached - the game is lost or won, and then the 
corresponding `CompletableFuture` is resolved.

## The motion state calculation & updates

There are several interlinked classes that are used to work with motion
update, both in game itself and motion simulation within the bot. The 
ball state is contained in a `MotionState`, which specified position and 
velocity of the ball.

The main interface in calculations is `MotionHandler`, which, given 
current state and desired time increment, provides next ball state and
its status - whether it is still moving, has stopped, has drowned, 
escaped the field or hit the target. Default `MotionHandler` 
implementation, `StandardMotionHandler`, is a simple wrapper above 
`MotionCalculator`, a type that predicts only the next position without 
actually interpreting it.

There is also `MovementPlotter` type, which is not within the engine 
itself but may be useful for simulations. Given a ball state and a step,
it plots movement and calls reducing function on each step, allowing 
easy calculations over values like minimum distance to hole given 
specific shot. The default implementation, `StandardMovementPlotter`,
utilises `MotionHandler` in a simple while loop to provide this 
functionality.

## Ball state prediction

Within the `MotionHandler` an `ODESolver` is used to approximate next 
ball velocity and position. `ODESolver` is an interface by itself, with 
three implementations: Euler, RK2 and RK4, which implement corresponding
numerical methods.
