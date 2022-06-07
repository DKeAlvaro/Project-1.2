# Pathfinding

The crucial thing in phase 3 of the project is the pathfinding 
algorithm. Given a maze-like map in which hole can't be reached in a 
single shot, the bot has to plot and perform several shots that would 
allow it to eventually score a hit.    

## Theory

Virtually any pathfinding algorithm, except for the ones relying on 
luck and heuristics on making good shot without actually trying to prove
they're doing something that will converge, would operate in two steps / 
would base on two distinct concepts. The first one would be finding 
waypoints (locations in which ball can come to a stop), while the second
one would connect those waypoints in a way that would result in a path
from start point to the hole. These waypoints and connections may be 
calculated ahead of time before plotting an actual path (for example, 
when minimal cost solution is pursued or when it is cheap to do so) or 
may be explored right in the process of finding such path (for example, 
when any solution would be sufficient).

## Waypoints detection and generation

There are different approaches that may result in a set of waypoints.
There are some common rules to adhere:

- Even though the space is represented by numbers that are discrete, the
set of possible points is so big that the space has to be treated as 
continuous, and it's not possible to evaluate each point 
- A waypoint can't be inside an obstacle
- A waypoint can't be on a slope that is too steep for ball to 
completely stop
- An algorithm may be interested not just in a point - it can be 
interested in an area plotted on the surface sharing same features
(with an optional "sweet point" to target) that satisfies other 
properties of waypoint as a whole (technically that's a set of densely 
placed waypoints). This would be further called a _wayspot_.

### Distanced neighbor detection

One of the simplest approaches is given a waypoint (wayspot) find the 
next point in a distance that is greater than some threshold (find the 
next non-intersecting wayspot, possibly with a distance gap controlled 
by similar threshold), then  repeat the process until the whole 
map is covered (or while it is possible to go in selected direction, 
for example). The threshold controls number of waypoints generated, 
as well as precision of the algorithm that will further use the found 
waypoints/wayspots.

This project does not explore this approach.

### Grid

Another approach is to split the map in XY plane as a grid of 
rectangular cells and treat each as a wayspot or search for one within
a cell. The simplest approach to evaluating whether cell is a wayspot 
is:

- Checking whether it intersects with obstacles
- Checking if there is a point with slope that allows ball stopping / if
the majority of area allows ball stopping / if there is no point with 
slope that prevents ball from stopping (of course, those calculations 
should be approximated instead of checking every point of a cell)

## Connecting waypoints

Given the waypoint/wayspot detection function, the next step is to find 
possible connections between the waypoint/wayspots to reach a solution
of continuous path between original ball position and the hole; in other 
words, a path-finding problem.

### A*

The most common approach to the problem is classic A* algorithm. A* 
implies continuous exploration of the waypoint/wayspot graph with 
maintaining two data structures, a set of nodes to be explored, and a 
table containing each explored node and the node it is most beneficial 
to come to this node from (the parent in the shortest way to this 
particular node). During the exploration of the nodes, if a shorter 
(cheaper) path is found, then table of explored node is updated, thus at
the end the table always contains the cheapest path.

A* can be formally described as:

- Start with set of unexplored nodes containing the start position,
empty previous node table and a heuristic function that gives a score 
for a node in terms of its benefit towards reaching the goal (for 
example, the distance from node to the ending goal)
- While set of unexplored nodes is not empty, extract a node with the 
lowest heuristic score from it
- If the node in scope is the goal, reconstruct the path using the table
and return it
- Otherwise, explore neighbors of the node in scope, adding them to the 
unexplored set and updating their scores in table. If neighbor is not 
yet in table or the distance from the start to the node in scope plus
distance between the two nodes is greater than recorded, update the 
record in table with calculated value.
- If unexplored node set was exhausted, consider the problem unsolvable.

However, A* has a drawback: it explores moving from one waypoint/wayspot
only to adjacent one. In the terms of golf, that would mean making a lot
of small hits to move from one small square to another, and that not 
only weakly corresponds to the pursued goal, but also may make maps with 
steep slopes unsolvable.

### Step reduction

Given a found path with too many steps, either by A* or other algorithm,
it makes sense to check if some of those steps are redundant because it 
is possible to get to the next node directly from the previous one. To 
do so, implementation has to derive a condition function that would tell 
whether it is possible to go from one node to another and then apply it
in a selected technique to find the shortest possible path.

#### Condition function

Given the nature of the project, the simplest approach for such 
condition function is just to reuse existing infrastructure to find 
whether it is possible from the first node to arrive and stop at the 
second node through hitting the ball when it achieves resting position.
The implementation should also not only check whether it is possible to 
go in one hit, but in a series of hits: some environments can be solved
in a number of hits, for example, a thin long corridor with 
perpendicular slope will force the ball to stop by the wall several
times, and the placements won't be considered as wayspots by naive area 
classification approach (since those stops would be by the obstacle). 
For making such a list of hits condition function needs to use a hit
optimizer, find best shot using it towards the goal (second wayspot),
project it and check whether it would stop at the wayspot (then it's the
last required shot in series), and if not, whether it resulted in 
significant improvement in distance to the second wayspot (if it does, 
it makes sense to continue making the best shots until the goal is 
reached, if it doesn't, then there is probably no feasible way from the 
first wayspot to second).
Obviously, after plotting the hits from one point to another it makes 
sense to store them and expected stop locations, so this information can 
be reused at the step of actually following the projected path.

Such approach is greedy for computational resources though, and it makes
sense to use less precise computation (i.e. smaller step size for ODE 
solver) for the condition function. This may lead to different behavior
while trying to adhere to found path, but this should be considered a 
normal situation, in which bot should reassess current situation and 
plot a new way, and in fact this only adds resilience to the bot.

#### Reduction techniques

##### Linear exclusion

This technique simply checks whether the next node in path can be 
skipped and the second next node can be reached from current one. The 
formalized algorithm is:

- Given a path in a format of list of nodes (source list)
- Remove first node and consider it as current in scope
- Initialize empty list for resulting path
- If there are less than two nodes remaining, append node in scope and 
the remainder to the resulting path list and return the resulting path
- Otherwise, check if condition function returns true for the node in 
scope and second node in the source list
- If condition holds, remove the first node from the source list and 
repeat with shrunk source list
- If it doesn't hold, append the node in scope to the resulting list,
then remove the first node from the source list and consider it as a
current in scope, then repeat with shrunk source list until the loop 
terminates due to the condition specified above

##### Binary search

This approach usually provides the same result (except for weird cases 
when it's possible to, starting with node A, to arrive at node D but not
node C preceding node D) but with quicker convergence. The algorithm is
described in following way:

- Given path in the format of list of nodes (source list)
- Remove first node and consider it current in scope
- Start loop
- If there are less than two nodes remaining in the source list, append
current node in scope and the remainder to the resulting list and return
it
- Apply binary search to the source list to find the latest node current
one is connected to
- Append the current node in scope to the resulting list
- Consider the found node to be in scope
- Shrink source list to the nodes coming after the found node
- Repeat until loop exit

##### Full elimination

The approaches listed above are following find-first strategy, implying
that their method of finding nodes will already provide good enough 
result. For the sake of this project it is true, but in general it may 
be desired to find the perfect solution. In that case it is not enough 
to just find a single combination that can't be further reduced, and
full analysis of all solutions is necessary. It can be achieved by 
branch-and-bound approach, by analysing all possible combinations for
specific node and following ones and then selecting the one with the 
lowest number of steps. 

It is not expected that this approach would beneficial for any but the
most sophisticated courses, while its worst-case complexity is O(n^2),
and because of that it is listed here only for completeness without 
practical testing. 

### A* with connection exploration

All the information listed above allows only finding paths initially 
consisting of sibling wayspots, each being a position good for stopping.
That prevents from forming paths formed of disconnected wayspots - for
example, if there is a steep slope between two wayspots, then the latter 
one is considered to be unreachable, even though a slightly more strong
hit would allow to climb up the slope and arrive at the second wayspot.

The following approach allows solving this problem by extending A* and 
requiring differentiation between _stopping_ and _passable_ wayspots. 
The first ones allow to stop at the point, while the second ones 
doesn't allow stopping, but don't contain obstacles and by thus 
theoretically can be passed without stopping.

The A* algorithm is used as before with the following changes:

- When a node is about to be added to the table, then its 
_first stoppable parent_ is used instead of direct parent (or both are 
stored for completeness). The first stoppable parent is obtained by 
considering current parent as node in scope and then replacing this node
scope with its direct parent as long as node in scope is not stoppable.
Since the exploration starts at the static ball position, it is 
guaranteed that such parent exists.
- The path reconstruction and following extra wayspot elimination 
proceeds as usual.

## Adaptive computation

The ability to find a path on a surface and the execution time are both 
dependent on possible waypoints. Small number of waypoints may result in 
a loss of solution, high number of waypoints guarantees long execution
time. The minimal number of waypoints required to solve a terrain is 
highly dependent on terrain and can't be computed directly, and because 
of that it is beneficial to take iterative approach, on each fail 
splitting map into larger number of waypoints until reaching certain 
threshold.

## Error correction

To achieve required goal, it is enough for bot to compute path once and 
then just follow it, reusing computation result on consecutive hits. 

However, due to the approximation reasons it is possible in this 
particular application that during the execution the ball would wander 
off the projected path. In that case the bot should detect that the ball 
has stopped off-waypoint and just compute the shortest path from current
point. While doing so, it can reuse existing computation to speed up
process. This may involve a bit of complexity regarding the shortest 
path table, since it contains values for initial ball position, but with
a bit of additional scaffolding it can be greatly simplified.
