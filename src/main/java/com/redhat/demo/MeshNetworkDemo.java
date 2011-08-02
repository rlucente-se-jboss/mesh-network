package com.redhat.demo;

import java.io.BufferedReader;
import java.io.FileReader;

import org.apache.log4j.Logger;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

/**
 * @author rlucente
 */
class MeshNetworkDemo extends BasicGame {
	private static final Logger LOG = Logger.getLogger(MeshNetworkDemo.class);

	private Links links;
	private Node selectedNode = null;

	/**
	 * @param params
	 */
	public MeshNetworkDemo() {
		super((String) Parameters.TITLE.getValue());
		links = new Links();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.newdawn.slick.BasicGame#init(org.newdawn.slick.GameContainer)
	 */
	@Override
	public void init(GameContainer gc) throws SlickException {
		int id = 0;

		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					(String) Parameters.NODE_FILENAME.getValue()));
			while (reader.ready()) {
				String line = reader.readLine().trim();

				if (!line.startsWith("#")) {
					String[] tokens = line.split("\\s+");

					NodeType type = NodeType.valueOf(tokens[0]);
					boolean antennaEnabled = Boolean.parseBoolean(tokens[1]);
					Vector2f position = new Vector2f(
							Float.parseFloat(tokens[2]),
							Float.parseFloat(tokens[3]));

					Image image = (Image) Parameters.SOLDIER_IMAGE.getValue();

					switch (type) {
					case EDGENODE:
						image = (Image) Parameters.EDGENODE_IMAGE.getValue();
						break;
					case HMMWV:
						image = (Image) Parameters.HMMWV_IMAGE.getValue();
						break;
					}

					links.addNode(new Node(id++, type, image, antennaEnabled,
							position));
				}
			}
			
			reader.close();
			links.finishNodes();
		} catch (Throwable t) {
			SlickException se = new SlickException("unable to initialize");
			se.initCause(t);
			throw se;
		}

		gc.getInput().addMouseListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.newdawn.slick.BasicGame#update(org.newdawn.slick.GameContainer,
	 * int)
	 */
	@Override
	public void update(GameContainer gc, int delta) throws SlickException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.newdawn.slick.Game#render(org.newdawn.slick.GameContainer,
	 * org.newdawn.slick.Graphics)
	 */
	public void render(GameContainer gc, Graphics g) throws SlickException {
		((Image) Parameters.BACKGROUND_IMAGE.getValue()).draw(0, 0);
		links.render(g);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.newdawn.slick.BasicGame#isAcceptingInput()
	 */
	@Override
	public boolean isAcceptingInput() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.newdawn.slick.BasicGame#mouseDragged(int, int, int, int)
	 */
	@Override
	public void mouseDragged(int oldx, int oldy, int newx, int newy) {
		if (selectedNode != null) {
			int deltaX = newx - oldx;
			int deltaY = newy - oldy;
			selectedNode.move(deltaX, deltaY);
			links.update(selectedNode);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.newdawn.slick.BasicGame#mousePressed(int, int, int)
	 */
	@Override
	public void mousePressed(int button, int x, int y) {
		selectedNode = links.findIntersectingNode(x, y);
		if (button == 1 && selectedNode != null) {
			selectedNode.toggleAntenna();
			links.update(selectedNode);
			selectedNode = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.newdawn.slick.BasicGame#mouseReleased(int, int, int)
	 */
	@Override
	public void mouseReleased(int button, int x, int y) {
		selectedNode = null;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			AppGameContainer app = new AppGameContainer(new MeshNetworkDemo());

			app.setDisplayMode((Integer) Parameters.WINDOW_WIDTH.getValue(),
					(Integer) Parameters.WINDOW_HEIGHT.getValue(), false);
			app.start();
		} catch (Exception e) {
			LOG.error("Abnormal demo exit", e);
		}
	}
}
