/**
 * 
 */
package com.redhat.demo;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
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
	private Edge[][] edges;

	/**
	 * @param node
	 */
	void addNode(Node node) {
		nodeList.add(node);
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

		Set<Node> visited = new HashSet<Node>();
		for (Node nodei : nodeList) {
			if (nodei.getType() == NodeType.EDGENODE) {
				renderCloud(nodei, visited, g);
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
	}

	/**
	 * @param nodei
	 * @param nodej
	 * @return
	 */
	private float calcAlpha(float dist) {
		int maxLinkRange = (Integer) Parameters.MAX_LINK_RANGE.getValue();
		if (dist <= maxLinkRange) {
			float percentDropoff = dist / maxLinkRange;
			return 1.0f - percentDropoff * percentDropoff;
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
			float alpha = calcAlpha(length);

			if (alpha != 0.0f) {
				float width = alpha * (Integer) Parameters.MAX_LINK_WIDTH.getValue();

				Color color = new Color(0, 0, 255, alpha);
				Shape linkPoly = createLinkPoly(posi, posj, length, width);
				ShapeFill linkFill = new GradientFill(posi, color, posj, color,
						false);

				float cloudWidth =
					(Float) Parameters.CLOUDLINK_SCALE.getValue() * width;
				float cloudAlpha = 0.75f * alpha;

				// only red edges for backhaul connections
				if (   nodei.getType() == NodeType.EDGENODE
				    || nodej.getType() == NodeType.EDGENODE) {
					cloudWidth = width;
					cloudAlpha = alpha;
					linkPoly = null;
					linkFill = null;
				}

				color = new Color(255, 0, 0, cloudAlpha);
				Shape cloudPoly = createLinkPoly(posi, posj, length, cloudWidth);
				ShapeFill cloudFill = new GradientFill(posi, color, posj,
						color, false);

				result = new Edge(linkPoly, cloudPoly, linkFill, cloudFill);
			}
		}

		return result;
	}

	/**
	 * @param posi
	 * @param posj
	 * @param alpha
	 * @return
	 */
	private Shape createLinkPoly(Vector2f posi, Vector2f posj, float length,
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
	 * @param nodei
	 * @param visited
	 * @param g
	 */
	private void renderCloud(Node nodei, Set<Node> visited, Graphics g) {
		visited.add(nodei);

		Set<Node> pendingNodes = new HashSet<Node>();
		for (Node nodej : nodeList) {

			if (!visited.contains(nodej)) {
				int i = nodei.getId();
				int j = nodej.getId();

				if (edges[i][j] != null) {
					edges[i][j].renderCloud(g);

                                        if (nodej.getType() != NodeType.SOLDIER) {
						pendingNodes.add(nodej);
                                        }
				}
			}
		}

		for (Node node : pendingNodes) {
			renderCloud(node, visited, g);
		}
	}

	/**
	 * @author rlucente
	 * 
	 */
	class Edge {
		private Shape linkPoly;
		private Shape cloudPoly;
		private ShapeFill linkFill;
		private ShapeFill cloudFill;

		/**
		 * @param linkPoly
		 * @param cloudPoly
		 * @param linkFill
		 * @param cloudFill
		 */
		Edge(Shape linkPoly, Shape cloudPoly, ShapeFill linkFill,
				ShapeFill cloudFill) {
			this.linkPoly = linkPoly;
			this.cloudPoly = cloudPoly;
			this.linkFill = linkFill;
			this.cloudFill = cloudFill;
		}

		/**
		 * @param g
		 */
		void render(Graphics g) {
			if (linkPoly != null && linkFill != null) {
				g.fill(linkPoly, linkFill);
			}
		}

		/**
		 * @param g
		 */
		void renderCloud(Graphics g) {
			g.fill(cloudPoly, cloudFill);
		}
	}
}
