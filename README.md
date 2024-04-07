Introduction:
-
Solver made for the technical interview of the driverless team at Formula Electric Belgium (FEB).
Calculates the shortest path (starting at the top row and ending at the bottom row) across a track given in a .txt file defined by the characters x and 0 in a rectangular grid) using the Dijkstra algorithm.

Initially a branch and bound algorithm was used to generate and traverse a search tree towards the optimal solution.
Pruning can be achieved in this tree by making sure current cost (of the partial path) combined with the remaining distance to the bottom row is lower then the current best cost.
Further we can also prune if we have found a previous shorter path to a certain node as all weights in the constructed graph are positive (either 1 or the sqaure root of 2).
To make this efficient it is necessary to get an initial (but non optimal solution) quickly, using this knowledge a simple heuristic was used for the initial lowerbounds for each node. These lowerbounds were initialized as the distance to the bottom row in a straight line. In preprocessing all neighbors of each node were sorted in ascending order based on there lowerbounds to make sure the car trends downwards.

While this algorithm succeeded to find the optimal solution the processing time was rather large. Therefor an approach using the Dijkstra algorithm was used. Using this approach the processing time was significantly faster by approximately an order of magnitude.
