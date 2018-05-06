# PeerSim Bellman-Ford Routing Protocol
This piece of coursework focuses on implementing and simulating a distributed prototype of Distance-vector Routing protocol with the Bellman-Ford algorithm in PeerSim.

## Use
Running PeerSim (in ./lib) is done using Java with the following configuration.

* Main class: peersim.Simulator (imports from ./lib)
* Program arguments: ./config/dv-random.txt (or another config)
* Working directory: ./

## Background
### PeerSim
PeerSim is a tool to simulate Peer-to-Peer (P2P) networks. Due to it being incredibly scalable, it can be used to simulate networks of a very large scale (e.g. millions of nodes) [[1]](http://peersim.sourceforge.net/). It is also useful in this assignment as it allows to simulate a network using a cycle-driven approach. As a result, the protocol can have multiple stages – in this case, initialisation, broadcast and computation.

### Bellman-Ford
The Bellman-Ford algorithm is an algorithm that calculates the shortest paths from any single source vertex to all other vertices in a weighted digraph [[2]](http://www.cs.rhul.ac.uk/books/dbook/main.pdf). Compared to Dijkstra’s algorithm, the Bellman-Ford algorithm tends to be slower for the same problem. However, it is more versatile in the sense that edge weights (i.e. costs) can be negative numbers.

## Implementation
The Link-state protocol class from a previous lab was reused as a base for the new protocol. The main difference between the two protocols is refactoring and the `compute()` method.

## Testing
The protocol was tested using different topologies supported by PeerSim. The resulting graph and table below are the minimum distances for a random network topology.

![Bellman Ford Sample](https://i.imgur.com/JiFZLVL.png)
```
ND#  0->|   0,  1,  2,  3,  4, |
    cost|   0,  5,  4,  1,  3, |
     via|   0,  0,  3,  0,  3, |
--------------------------------
ND#  1->|   0,  1,  2,  3,  4, |
    cost|   5,  0,  7,  4,  2, |
     via|   1,  1,  3,  4,  1, |
--------------------------------
ND#  2->|   0,  1,  2,  3,  4, |
    cost|   4,  7,  0,  3,  5, |
     via|   3,  4,  2,  2,  3, |
--------------------------------
ND#  3->|   0,  1,  2,  3,  4, |
    cost|   1,  4,  3,  0,  2, |
     via|   3,  4,  3,  3,  3, |
--------------------------------
ND#  4->|   0,  1,  2,  3,  4, |
    cost|   3,  2,  5,  2,  0, |
     via|   3,  4,  3,  4,  4, |
--------------------------------
``` 
Further testing was done with larger networks as well as other topologies, such as tree and star. The resulting paths were identical to the previous protocol with Djikstra's algorithm.

## Further Improvements
Based on feedback, the protocol's `broadcast()` method should be modified to share calculated distances as opposed to neighbouring links.