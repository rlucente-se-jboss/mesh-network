/**
 * 
 */
package com.redhat.demo;

/**
 * @author rlucente
 * 
 */
public enum NodeType {
	SOLDIER(false, false) {
		/*
		 * (non-Javadoc)
		 * 
		 * @see com.redhat.demo.NodeType#connectsTo(com.redhat.demo.NodeType)
		 */
		@Override
		boolean connectsTo(NodeType type) {
			if (type == HMMWV) {
				return true;
			}
			return false;
		}
	},
	HMMWV(true, false) {
		/*
		 * (non-Javadoc)
		 * 
		 * @see com.redhat.demo.NodeType#connectsTo(com.redhat.demo.NodeType)
		 */
		@Override
		boolean connectsTo(NodeType type) {
			return true;
		}
	},
	EDGENODE(true, true) {
		/*
		 * (non-Javadoc)
		 * 
		 * @see com.redhat.demo.NodeType#connectsTo(com.redhat.demo.NodeType)
		 */
		@Override
		boolean connectsTo(NodeType type) {
			if (type == HMMWV) {
				return true;
			}
			return false;
		}
	};

	private boolean isBroker;
	private boolean isCloudEdge;

	/**
	 * @param isBroker
	 * @param isCloudEdge
	 */
	NodeType(boolean isBroker, boolean isCloudEdge) {
		this.isBroker = isBroker;
		this.isCloudEdge = isCloudEdge;
	}

	/**
	 * @param type
	 * @return
	 */
	abstract boolean connectsTo(NodeType type);

	/**
	 * @return
	 */
	boolean isBroker() {
		return isBroker;
	}

	/**
	 * @return
	 */
	boolean isCloudEdge() {
		return isCloudEdge;
	}
}
