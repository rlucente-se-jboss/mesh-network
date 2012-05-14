/**
 * 
 */
package com.redhat.demo;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.KruskalMinimumSpanningTree;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

/**
 * @author rlucente
 * 
 */
class NodeGraph {
	private Graph<Vertex, Edge> graph = new SimpleGraph<Vertex, Edge>(
			Edge.class);
	private List<Vertex> vertexRenderOrder = new LinkedList<Vertex>();
	private Set<Edge> spanningTree = new HashSet<Edge>();
	private Vertex activeCloudNode = null;

	/**
	 * @param vertex
	 */
	void addVertex(Vertex vertex) {
		graph.addVertex(vertex);
		vertexRenderOrder.add(vertex);
	}

	/**
	 * @param x
	 * @param y
	 * @return
	 */
	Vertex findIntersectingVertex(int x, int y) {
		for (Vertex vertex : graph.vertexSet()) {

			if (vertex.isSelected(x, y)) {
				// make sure selected vertex is rendered last
				vertexRenderOrder.remove(vertex);
				vertexRenderOrder.add(vertex);
				return vertex;
			}
		}

		return null;
	}

	/**
	 * @param g
	 */
	void render(Graphics g) {
		for (Edge edge : graph.edgeSet()) {
			edge.render(g, spanningTree.contains(edge));
		}

		for (Vertex vertex : vertexRenderOrder) {
			vertex.render(g);
		}
	}

	/**
	 * @param nodei
	 */
	void update() {
		activeCloudNode = null;

		// recalculate all edges
		for (Vertex v1 : graph.vertexSet()) {
			for (Vertex v2 : graph.vertexSet()) {
				graph.removeEdge(v1, v2);

				// find active "cloud edge" node as start for min spanning tree
				if (addEdge(v1, v2) && v1.isCloudEdge()) {
					activeCloudNode = v1;
				}
			}
		}

		// find G(V,E) connected to active "cloud edge" where v is only brokers
		if (activeCloudNode != null) {
			WeightedGraph<Vertex, Edge> brokerGraph = new SimpleWeightedGraph<Vertex, Edge>(
					Edge.class);
			findReachableBrokers(activeCloudNode, brokerGraph);

			// using that graph, find spanning tree using Kruskal's algorithm
			KruskalMinimumSpanningTree<Vertex, Edge> kmst = new KruskalMinimumSpanningTree<Vertex, Edge>(
					brokerGraph);
			spanningTree = kmst.getEdgeSet();

			// try to connect each soldier to strongest broker in spanning tree
			for (Vertex v : graph.vertexSet()) {
				if (!v.isBroker()) {
					Edge minEdge = null;

					for (Edge e : graph.edgesOf(v)) {
						Vertex u = e.getOtherVertex(v);

						if (   brokerGraph.containsVertex(u)
						    && (   minEdge == null
                                                        || (minEdge.getWeight() > e.getWeight()))) {
							minEdge = e;
						}
					}

					if (minEdge != null) {
						spanningTree.add(minEdge);
					}
				}
			}
		}
	}

	/**
	 * @param v1
	 * @param v2
	 */
	private boolean addEdge(Vertex v1, Vertex v2) {
		boolean result = false;

		if (!graph.containsEdge(v1, v2) && !graph.containsEdge(v2, v1)
				&& v1.connectsTo(v2)) {
			Vector2f pos1 = v1.getCenter();
			Vector2f pos2 = v2.getCenter();

			float opacity = calculateLinkOpacity(pos1, pos2);

			if (opacity != 0.0f) {
				Edge edge = new Edge(v1, v2, opacity);
				graph.addEdge(v1, v2, edge);
				result = true;
			}
		}

		return result;
	}

	/**
	 * @param dist
	 * @return
	 */
	private float calculateLinkOpacity(Vector2f pos1, Vector2f pos2) {
		int maxLinkRange = (Integer) Parameters.MAX_LINK_RANGE.getValue();
		float dist = pos1.distance(pos2);

		if (dist <= maxLinkRange) {
			float percentDropoff = dist / maxLinkRange;
			float opacity = 1.0f - percentDropoff * percentDropoff;

			float minOpacity = (Float) Parameters.MIN_OPACITY.getValue();
			return (opacity > minOpacity) ? opacity : minOpacity;
		}

		return 0.0f;
	}

	/**
	 * 
	 * @param v
	 * @param g
	 * @param reachable
	 */
	private void findReachableBrokers(Vertex v,
			WeightedGraph<Vertex, Edge> weightedGraph) {
		for (Edge e : graph.edgesOf(v)) {

			if (e.hasOnlyBrokers() && !weightedGraph.containsEdge(e)) {
				Vertex u = e.getOtherVertex(v);

				weightedGraph.addVertex(v);
				weightedGraph.addVertex(u);
				weightedGraph.addEdge(u, v, e);
				weightedGraph.setEdgeWeight(e, e.getWeight());

				findReachableBrokers(u, weightedGraph);
			}
		}
	}
}
