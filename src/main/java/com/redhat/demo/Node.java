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
class Node {

	private int id;
	private Image image;
	private Vector2f position;
	private Vector2f center;
	private Shape boundingBox;
	private NodeType type;
	private boolean antennaEnabled;

	/**
	 * @param id
	 * @param type
	 * @param image
	 * @param antennaEnabled
	 * @param position
	 */
	Node(int id, NodeType type, Image image, boolean antennaEnabled,
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
	 * @param node
	 * @return
	 */
	boolean connectsTo(Node node) {
		return antennaEnabled && node.antennaEnabled && (this != node)
				&& type.connectsTo(node.getType());
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
	NodeType getType() {
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
