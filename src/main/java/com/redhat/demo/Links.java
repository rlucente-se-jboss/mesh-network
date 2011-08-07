/**
 * 
 */
package com.redhat.demo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.ShapeFill;
import org.newdawn.slick.fills.GradientFill;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;

/**
 * @author rlucente
 * 
 */
class Links {
	private List<Node> nodeList = new LinkedList<Node>();
	private Map<Integer, Node> nodeById = new HashMap<Integer, Node>();
	private Edge[][] edges;

	/**
	 * @param node
	 */
	void addNode(Node node) {
		nodeList.add(node);
		nodeById.put(node.getId(), node);
	}

	/**
	 * 
	 */
	void finishNodes() {
		int size = nodeList.size();
		edges = new Edge[size][size];

		for (Node nodei : nodeList) {
			update(nodei);
		}
	}

	/**
	 * @param x
	 * @param y
	 * @return
	 */
	Node findIntersectingNode(int x, int y) {
		for (Node node : nodeList) {
			if (node.isSelected(x, y)) {
				nodeList.remove(node);
				nodeList.add(node);
				return node;
			}
		}

		return null;
	}

	/**
	 * @param g
	 */
	void render(Graphics g) {
		for (int i = 0; i < nodeList.size(); i++) {
			for (int j = i + 1; j < nodeList.size(); j++) {
				if (edges[i][j] != null) {
					edges[i][j].render(g);
				}
			}
		}

		for (Node node : nodeList) {
			node.render(g);
		}
	}

	/**
	 * @param nodei
	 */
	void update(Node nodei) {
		int i = nodei.getId();

		for (Node nodej : nodeList) {
			int j = nodej.getId();

			edges[i][j] = createEdge(nodei, nodej);
			edges[j][i] = edges[i][j];
		}

		labelSpanningTree();
	}

	/**
	 * @param dist
	 * @return
	 */
	private float calculateLinkOpacity(float dist) {
		int maxLinkRange = (Integer) Parameters.MAX_LINK_RANGE.getValue();
		float minOpacity = (Float) Parameters.MIN_OPACITY.getValue();

		if (dist <= maxLinkRange) {
			float percentDropoff = dist / maxLinkRange;
			float opacity = 1.0f - percentDropoff * percentDropoff;
			return (opacity > minOpacity) ? opacity : minOpacity;
		}

		return 0.0f;
	}

	/**
	 * @param nodei
	 * @param nodej
	 */
	private Edge createEdge(Node nodei, Node nodej) {
		Edge result = null;

		if (nodei.connectsTo(nodej)) {
			Vector2f posi = nodei.getCenter();
			Vector2f posj = nodej.getCenter();

			float length = posi.distance(posj);
			float opacity = calculateLinkOpacity(length);

			if (opacity != 0.0f) {
				float width = opacity
						* (Integer) Parameters.MAX_LINK_WIDTH.getValue();

				Color color = new Color(0, 0, 255, opacity);
				Shape edgePoly = createEdgePoly(posi, posj, length, width);
				ShapeFill edgeFill = new GradientFill(posi, color, posj, color,
						false);

				color = new Color(255, 0, 0, opacity);
				ShapeFill spanningTreeFill = new GradientFill(posi, color,
						posj, color, false);

				result = new Edge(edgePoly, edgeFill, spanningTreeFill);
			}
		}

		return result;
	}

	/**
	 * @param posi
	 * @param posj
	 * @param length
	 * @param width
	 * @return
	 */
	private Shape createEdgePoly(Vector2f posi, Vector2f posj, float length,
			float width) {
		float factor = 0.5f * width / length;

		float xdiff = posj.getX() - posi.getX();
		float ydiff = posj.getY() - posi.getY();

		float deltax = Math.abs(factor * ydiff);
		float deltay = Math.abs(factor * xdiff);

		Polygon poly = new Polygon();

		if (Math.signum(xdiff) == Math.signum(ydiff)) {
			poly.addPoint(posi.getX() - deltax, posi.getY() + deltay);
			poly.addPoint(posi.getX() + deltax, posi.getY() - deltay);
			poly.addPoint(posj.getX() + deltax, posj.getY() - deltay);
			poly.addPoint(posj.getX() - deltax, posj.getY() + deltay);
		} else {
			poly.addPoint(posi.getX() - deltax, posi.getY() - deltay);
			poly.addPoint(posj.getX() - deltax, posj.getY() - deltay);
			poly.addPoint(posj.getX() + deltax, posj.getY() + deltay);
			poly.addPoint(posi.getX() + deltax, posi.getY() + deltay);
		}

		return poly;
	}

	/**
         */
	private void labelSpanningTree() {
		for (Node nodei : nodeList) {
			int i = nodei.getId();

			for (Node nodej : nodeList) {
				int j = nodej.getId();

				if (edges[i][j] != null) {
					edges[i][j].setInSpanningTree(false);
				}
			}
		}

		Set<Node> explored = new HashSet<Node>();

		for (Node nodei : nodeList) {
			if (nodei.isCloudEdge()) {
				labelSpanningTree(nodei, explored);
			}
		}
	}

	/**
	 * @param nodei
	 * @param explored
	 */
	private void labelSpanningTree(Node nodei, Set<Node> explored) {
		explored.add(nodei);
		int i = nodei.getId();

		List<Integer> columnIds = sortRowByOpacity(i);
		for (int j : columnIds) {
			Node nodej = nodeById.get(j);

			if (!explored.contains(nodej)) {
				edges[i][j].setInSpanningTree(true);

				if (nodej.isBroker()) {
					labelSpanningTree(nodej, explored);
				} else {
					explored.add(nodej);
				}
			}
		}
	}

	/**
	 * @param rowId
	 */
	private List<Integer> sortRowByOpacity(final int rowId) {
		List<Integer> sortedOpacity = new LinkedList<Integer>();

		for (int j = 0; j < nodeList.size(); j++) {
			Edge edge = edges[rowId][j];

			if (edge != null) {
				boolean inserted = false;
				for (int k = 0; !inserted && k < sortedOpacity.size(); k++) {
					if (edge.getOpacity() > edges[rowId][sortedOpacity.get(k)]
							.getOpacity()) {
						sortedOpacity.add(k, j);
						inserted = true;
					}
				}

				if (!inserted) {
					sortedOpacity.add(j);
				}
			}
		}

		return sortedOpacity;
	}

	/**
	 * @author rlucente
	 * 
	 */
	class Edge {
		private Shape edgePoly;
		private ShapeFill edgeFill;
		private ShapeFill spanningTreeFill;
		private boolean inSpanningTree;

		/**
		 * @param edgePoly
		 * @param edgeFill
		 * @param spanningTreeFill
		 */
		Edge(Shape edgePoly, ShapeFill edgeFill, ShapeFill spanningTreeFill) {
			this.edgePoly = edgePoly;
			this.edgeFill = edgeFill;
			this.spanningTreeFill = spanningTreeFill;
		}

		/**
		 * @return
		 */
		float getOpacity() {
			float x = edgePoly.getCenterX();
			float y = edgePoly.getCenterY();
			return edgeFill.colorAt(edgePoly, x, y).getAlpha() / 255.0f;
		}

		/**
		 * @param inSpanningTree
		 */
		void setInSpanningTree(boolean inSpanningTree) {
			this.inSpanningTree = inSpanningTree;
		}

		/**
		 * @param g
		 */
		void render(Graphics g) {
			if (inSpanningTree) {
				g.fill(edgePoly, spanningTreeFill);
			} else {
				g.fill(edgePoly, edgeFill);
			}
		}
	}
}
