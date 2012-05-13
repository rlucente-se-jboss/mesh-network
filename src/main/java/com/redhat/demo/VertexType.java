/**
 * 
 */
package com.redhat.demo;

/**
 * @author rlucente
 * 
 */
public enum VertexType {
	SOLDIER(false, false) {
		/*
		 * (non-Javadoc)
		 * 
		 * @see com.redhat.demo.NodeType#connectsTo(com.redhat.demo.NodeType)
		 */
		@Override
		boolean connectsTo(VertexType type) {
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
		boolean connectsTo(VertexType type) {
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
		boolean connectsTo(VertexType type) {
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
	VertexType(boolean isBroker, boolean isCloudEdge) {
		this.isBroker = isBroker;
		this.isCloudEdge = isCloudEdge;
	}

	/**
	 * @param type
	 * @return
	 */
	abstract boolean connectsTo(VertexType type);

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
