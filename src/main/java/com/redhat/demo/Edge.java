package com.redhat.demo;

import org.jgrapht.graph.DefaultWeightedEdge;
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
class Edge extends DefaultWeightedEdge {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Vertex v1;
	private Vertex v2;
	private float opacity;

	/**
	 * @param edgePoly
	 * @param edgeFill
	 * @param spanningTreeFill
	 */
	Edge(Vertex v1, Vertex v2, float opacity) {
		this.v1 = v1;
		this.v2 = v2;
		this.opacity = opacity;
	}

	/**
	 * 
	 */
	Vertex getOtherVertex(Vertex v) {
		if (v == v2) {
			return v1;
		} else {
			return v2;
		}
	}

	/**
	 * 
	 */
	Float getWeight() {
		return 1.0f - opacity;
	}

	/**
	 * 
	 */
	boolean hasOnlyBrokers() {
		return v1.isBroker() && v2.isBroker();
	}

	/**
	 * @param g
	 */
	void render(Graphics g, boolean inSpanningTree) {
		Vector2f pos1 = v1.getCenter();
		Vector2f pos2 = v2.getCenter();

		float length = pos1.distance(pos2);
		float width = opacity * (Integer) Parameters.MAX_LINK_WIDTH.getValue();

		Color color = new Color(0, 0, 255, opacity);
		if (inSpanningTree) {
			color = new Color(255, 0, 0, opacity);
		}

		Shape edgePoly = createPoly(pos1, pos2, length, width);
		ShapeFill edgeFill = new GradientFill(pos1, color, pos2, color, false);

		g.fill(edgePoly, edgeFill);
	}

	/**
	 * @param posi
	 * @param posj
	 * @param length
	 * @param width
	 * @return
	 */
	private Shape createPoly(Vector2f posi, Vector2f posj, float length,
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
}