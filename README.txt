OVERVIEW

This is a mesh network demo with a military focus.  The idea for this demo came
from the flash mesh demo at http://one.laptop.org/about/hardware, which
demonstrates the XO laptop's features for a self-forming mesh network.

This application leverages the Lightweight Java Game Library (LWJGL) and the
Slick - 2D Game Library.  Extensive documentation, downloads, etc for both of
these tools can be found at:

http://www.lwjgl.org/
http://slick.cokeandcode.com/

POLICY

The demo has a simple policy governing how connections are formed and
displayed: 

1.  Soldiers can communicate only with HMMWVs (implying the need for a broker)
2.  HMMWVs can communicate with any other node
3.  Cloud edge nodes, representing connections to long haul infrastructure, can
    communicate with HMMWVs (implying a broker-to-broker connection)
4.  A connection between two nodes is represented as a line where the thickness
    and opaqueness indicate signal strength
5.  A connection thats part of the spanning tree to the cloud edge is red,
    whereas redundant connections are blue.  Nodes that are isolated from the
    cloud edge will only have blue connections.

BUILD AND RUN

This demo relies on maven and java so please make sure that both are installed
in your environment.  To build and run the demo:

mvn clean install
mvn exec:exec -Dlwjgl.os.name=linux

Valid values for lwjgl.os.name are linux, macosx, solaris, and windows, with
linux being the default.

USAGE

The demo displays HMMWVs, soldiers, and cloud edge nodes connected via a mesh
network.  With your mouse, left-click and drag nodes around to change their
connections to other nodes.  You can toggle communications for a node by
right-clicking.  All of these changes will result in changes to the spanning
tree showing the connections to the cloud edge.

Enjoy!! 
