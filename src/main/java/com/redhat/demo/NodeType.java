/**
 * 
 */
package com.redhat.demo;

/**
 * @author rlucente
 * 
 */
public enum NodeType {
	SOLDIER {
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.redhat.demo.NodeType#connectsTo(com.redhat.demo
		 * .NodeType)
		 */
		@Override
		boolean connectsTo(NodeType type) {
			if (type == HMMWV) {
				return true;
			}
			return false;
		}
	},
	HMMWV {
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.redhat.demo.NodeType#connectsTo(com.redhat.demo
		 * .NodeType)
		 */
		@Override
		boolean connectsTo(NodeType type) {
			return true;
		}
	},
	EDGENODE {
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.redhat.demo.NodeType#connectsTo(com.redhat.demo
		 * .NodeType)
		 */
		@Override
		boolean connectsTo(NodeType type) {
			if (type == HMMWV) {
				return true;
			}
			return false;
		}
	};

	/**
	 * @param type
	 * @return
	 */
	abstract boolean connectsTo(NodeType type);
}
