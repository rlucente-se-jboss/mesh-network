/**
 * 
 */
package com.redhat.demo;

import java.io.FileReader;
import java.io.Reader;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
 * @author rlucente
 * 
 */
enum Parameters {
	TITLE, BACKGROUND_IMAGE, SOLDIER_IMAGE, HMMWV_IMAGE, EDGENODE_IMAGE,
	SOLDIER_SCALE, HMMWV_SCALE, EDGENODE_SCALE, WINDOW_WIDTH, WINDOW_HEIGHT,
	MAX_LINK_RANGE, MAX_LINK_WIDTH, MIN_SCALE, CLOUDLINK_SCALE, NODE_FILENAME;

	private static final String PATH = "demo.properties";
	private static final Logger LOG = Logger.getLogger(Parameters.class);
	private static Properties properties;

	private Object value;

	/**
	 * 
	 */
	private void init() {
		if (properties == null) {
			properties = new Properties();
			try {
				Reader reader = new FileReader(PATH);
				properties.load(reader);
			} catch (Exception e) {
				LOG.fatal("Unable to load " + PATH + " file from classpath.", e);
				System.exit(1);
			}
		}

		String val = (String) properties.get(this.toString());

		switch (this) {
		case TITLE:
		case NODE_FILENAME:
			value = val;
			break;
		case BACKGROUND_IMAGE:
			int width = (Integer) WINDOW_WIDTH.getValue();
			int height = (Integer) WINDOW_HEIGHT.getValue();
			try {
				value = new Image(val).getScaledCopy(width, height);
			} catch (SlickException se) {
				LOG.fatal("Unable to load image for '" + this + "'");
				System.exit(1);
			}
			break;
		case SOLDIER_IMAGE:
			float scale = (Float) SOLDIER_SCALE.getValue();
			value = initImage(val, scale);
			break;
		case HMMWV_IMAGE:
			scale = (Float) HMMWV_SCALE.getValue();
			value = initImage(val, scale);
			break;
		case EDGENODE_IMAGE:
			scale = (Float) EDGENODE_SCALE.getValue();
			value = initImage(val, scale);
			break;
		case SOLDIER_SCALE:
		case HMMWV_SCALE:
		case EDGENODE_SCALE:
		case CLOUDLINK_SCALE:
		case MIN_SCALE:
			try {
				value = Float.valueOf(val);
			} catch (NumberFormatException nfe) {
				LOG.fatal("Parameter '" + this + "' is not a float value.");
				System.exit(1);
			}
			break;
		case WINDOW_WIDTH:
		case WINDOW_HEIGHT:
		case MAX_LINK_RANGE:
		case MAX_LINK_WIDTH:
			try {
				value = Integer.valueOf(val);
			} catch (NumberFormatException nfe) {
				LOG.fatal("Parameter '" + this + "' is not an integer value.");
				System.exit(1);
			}
		}
	}

	/**
	 * @param imageFilename
	 * @param scale
	 * @return
	 */
	private Image initImage(String imageFilename, float scale) {
		Image result = null;
		try {
			result = new Image(imageFilename).getScaledCopy(scale);
		} catch (SlickException se) {
			LOG.fatal("Unable to read image file '" + imageFilename + "'");
			System.exit(1);
		}
		return result;
	}

	/**
	 * @return
	 */
	public Object getValue() {
		if (value == null) {
			init();
		}
		return value;
	}
}
