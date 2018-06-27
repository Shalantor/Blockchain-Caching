# Blockchain-Caching
Blockchain caching strategies and algorithms

# Abstract

In this project user interest driven caching strategies in environments where blockchain applications are used, were studied. 

For this purpose, a network of nodes running a blockchain application, where each node uses different caching strategies to evaluate blocks and to manage the available space in their local memory, was simulated. In this simulation each node has its own criteria, based on which it chooses the blocks which have the greatest personal value.

There are four different types of nodes, where some of them store whole blocks in their local memory, while others just store the information from the blocks which is important to them. 

Furthermore, a communication protocol was designed, in order to allow for information exchange and cooperation between the nodes.

In the above described network, results from running the application for its different parameters, caching strategies and node configurations were recorded and evaluated. These results include information about the number and the overall size of the saved blocks in each node as well as the percentage of the personally useful data each node has in its local memory in comparison to the overall data that it stores. From those results, conclusions about the most suitable application parameters and the most effective caching strategies can be made.

# Prerequisites

* Java version 1.8 or later
* MongoDB Database https://www.mongodb.com/
* Java MongoDB Driver https://mongodb.github.io/mongo-java-driver/
* External libraries like json.org and apache.commons are bundled in this project

# Node configurations
READMEs are provided with instructions on how to configure nodes with input files

# Getting Started

* For now open either with IntelliJ or Eclipse or compile manually (Will add gradle as build tool soon)
* For tests just run one of the test classes in the test folder.
