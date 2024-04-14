Introduction:
-
Solver made for the technical interview of the driverless team at Formula Electric Belgium (FEB).
Calculates the shortest path (starting at the top row and ending at the bottom row) across a track given in a .txt file defined by the characters x and 0 in a rectangular grid) using the Dijkstra algorithm.

Initially a branch and bound algorithm was used to generate and traverse a search tree towards the optimal solution.
Pruning can be achieved in this tree by making sure current cost (of the partial path) combined with the remaining distance to the bottom row is lower then the current best cost.
Further we can also prune if we have found a previous shorter path to a certain node as all weights in the constructed graph are positive (either 1 or the sqaure root of 2).
To make this efficient it is necessary to get an initial (but non optimal solution) quickly, using this knowledge a simple heuristic was used for the initial lowerbounds for each node. These lowerbounds were initialized as the distance to the bottom row in a straight line. In preprocessing all neighbors of each node were sorted in ascending order based on there lowerbounds to make sure the car trends downwards.

While this algorithm succeeded to find the optimal solution the processing time was rather large. Therefor an approach using the Dijkstra algorithm was used. Using this approach the processing time was significantly faster (approximately an order of magnitude).
To further improve the speed of this algorithm it was adapted to stop when it reaches the bottom row for the first time as this is always the shortest due to the usage of the priority queue. This reduced the processing time by approximately another 20 to 25% (disregarding any time necessary for preprocessing or printing of the result).
Lastly the A* heuristic was also added providing an estimate of the totalcost (cost to reach the bottom row for each node). While this approach did not significantly improve the speed of the algorithm only a very simple heuristic (distance to the bottom in a straight line) was used. By further experimenting with heuristics it would be possible to improve the speed of this algorithm further.
