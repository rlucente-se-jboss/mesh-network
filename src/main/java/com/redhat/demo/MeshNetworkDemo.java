package com.redhat.demo;

import java.io.BufferedReader;
import java.io.InputStreamReader;

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
public class MeshNetworkDemo extends BasicGame {

	// ignored for applets
	private static final String TITLE = "Red Hat Mesh Network Demo";

	private static GameContainer GAME_CONTAINER = null;
	
	private Image background;
	private NodeGraph graph;
	private Vertex selectedNode = null;
	
	/**
	 * @return
	 */
	public static GameContainer getContainer() {
		return GAME_CONTAINER;
	}

	/**
	 * @param gc
	 */
	private static void setGameContainer(GameContainer gc) {
		GAME_CONTAINER = gc;
	}
	
	/**
	 * @param params
	 */
	public MeshNetworkDemo() {
		super(TITLE);
		graph = new NodeGraph();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.newdawn.slick.BasicGame#init(org.newdawn.slick.GameContainer)
	 */
	@Override
	public void init(GameContainer gc) throws SlickException {
		MeshNetworkDemo.setGameContainer(gc);
		
		int id = 0;

		try {
			// get width and height from game container (so applet works)
			int width= gc.getWidth();
			int height= gc.getHeight();
			background = ((Image) Parameters.BACKGROUND_IMAGE.getValue())
				.getScaledCopy(width, height);

			String nodeFilename = "/"
					+ (String) Parameters.NODE_FILENAME.getValue();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					MeshNetworkDemo.class.getResourceAsStream(nodeFilename)));

			while (reader.ready()) {
				String line = reader.readLine().trim();

				if (!line.startsWith("#")) {
					String[] tokens = line.split("\\s+");

					VertexType type = VertexType.valueOf(tokens[0]);
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

					graph.addVertex(new Vertex(id++, type, image, antennaEnabled,
							position));
				}
			}

			reader.close();
			graph.update();
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
		background.draw(0, 0);
		graph.render(g);
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
			graph.update();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.newdawn.slick.BasicGame#mousePressed(int, int, int)
	 */
	@Override
	public void mousePressed(int button, int x, int y) {
		selectedNode = graph.findIntersectingVertex(x, y);
		if (button == 1 && selectedNode != null) {
			selectedNode.toggleAntenna();
			graph.update();
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
			MeshNetworkDemo.setGameContainer(app);
			
			app.setDisplayMode((Integer) Parameters.WINDOW_WIDTH.getValue(),
					(Integer) Parameters.WINDOW_HEIGHT.getValue(), false);
			app.start();
		} catch (Exception e) {
			throw new RuntimeException("Abnormal demo exit", e);
		}
	}
}
