/**
 * 
 */
package com.redhat.demo;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;

/**
 * @author rlucente
 * 
 */
class Vertex {

	private int id;
	private Image image;
	private Vector2f position;
	private Vector2f center;
	private Shape boundingBox;
	private VertexType type;
	private boolean antennaEnabled;

	/**
	 * @param id
	 * @param type
	 * @param image
	 * @param antennaEnabled
	 * @param position
	 */
	Vertex(int id, VertexType type, Image image, boolean antennaEnabled,
			Vector2f position) {
		this.id = id;
		this.type = type;
		this.image = image;
		this.antennaEnabled = antennaEnabled;
		this.position = position;
		boundingBox = new Rectangle(position.x, position.y, image.getWidth(),
				image.getHeight());
		center = new Vector2f(boundingBox.getCenter());
	}

	/**
	 * @param vertex
	 * @return
	 */
	boolean connectsTo(Vertex vertex) {
		return antennaEnabled && vertex.antennaEnabled && (this != vertex)
				&& type.connectsTo(vertex.getType());
	}

	/**
	 * @return
	 */
	Vector2f getCenter() {
		return center;
	}

	/**
	 * @return
	 */
	int getId() {
		return id;
	}

	/**
	 * @return
	 */
	Vector2f getPosition() {
		return position;
	}

	/**
	 * @return
	 */
	VertexType getType() {
		return type;
	}

	/**
	 * @return
	 */
	boolean isBroker() {
		return type.isBroker();
	}

	/**
	 * @return
	 */
	boolean isCloudEdge() {
		return type.isCloudEdge();
	}

	/**
	 * @param x
	 * @param y
	 * @return
	 */
	boolean isSelected(int x, int y) {
		return boundingBox.contains(x, y);
	}

	/**
	 * @param deltaX
	 * @param deltaY
	 */
	void move(int deltaX, int deltaY) {
		position.x += deltaX;
		position.y += deltaY;
		boundingBox.setLocation(position);
		center = new Vector2f(boundingBox.getCenter());
	}

	/**
	 * @param graphics
	 */
	void render(Graphics graphics) {
		image.draw(position.x, position.y);
	}

	/**
	 * 
	 */
	void toggleAntenna() {
		antennaEnabled = !antennaEnabled;
	}

	/**
	 * @param gc
	 * @param delta
	 */
	void update(GameContainer gc, int delta) {
	}
}
