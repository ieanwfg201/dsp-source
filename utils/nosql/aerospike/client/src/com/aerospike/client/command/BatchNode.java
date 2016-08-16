/* 
 * Copyright 2012-2014 Aerospike, Inc.
 *
 * Portions may be licensed to Aerospike, Inc. under one or more contributor
 * license agreements.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.aerospike.client.command;

import java.util.ArrayList;
import java.util.List;

import com.aerospike.client.AerospikeException;
import com.aerospike.client.Key;
import com.aerospike.client.ResultCode;
import com.aerospike.client.cluster.Cluster;
import com.aerospike.client.cluster.Node;
import com.aerospike.client.cluster.Partition;

public final class BatchNode {
	
	public static List<BatchNode> generateList(Cluster cluster, Key[] keys) throws AerospikeException {
		Node[] nodes = cluster.getNodes();
		
		if (nodes.length == 0) {
			throw new AerospikeException(ResultCode.SERVER_NOT_AVAILABLE, "Command failed because cluster is empty.");
		}
		
		int nodeCount = nodes.length;
		int keysPerNode = keys.length / nodeCount + 10;

		// Split keys by server node.
		List<BatchNode> batchNodes = new ArrayList<BatchNode>(nodeCount+1);
				
		for (int i = 0; i < keys.length; i++) {
			Key key = keys[i];
			Partition partition = new Partition(key);			
			BatchNode batchNode;
			
			Node node = cluster.getNode(partition);
			batchNode = findBatchNode(batchNodes, node);
			
			if (batchNode == null) {
				batchNodes.add(new BatchNode(node, keysPerNode, key.namespace, i));
			}
			else {
				batchNode.addKey(key.namespace, i);
			}
		}
		return batchNodes;
	}

	public final Node node;
	public final List<BatchNamespace> batchNamespaces;
	public final int keyCapacity;

	public BatchNode(Node node, int keyCapacity, String namespace, int offset) {
		this.node = node;
		this.keyCapacity = keyCapacity;
		batchNamespaces = new ArrayList<BatchNamespace>(4);
		batchNamespaces.add(new BatchNamespace(namespace, keyCapacity, offset));
	}
	
	public void addKey(String namespace, int offset) {
		BatchNamespace batchNamespace = findNamespace(namespace);
		
		if (batchNamespace == null) {
			batchNamespaces.add(new BatchNamespace(namespace, keyCapacity, offset));
		}
		else {
			batchNamespace.add(offset);
		}
	}
	
	private BatchNamespace findNamespace(String ns) {
		for (BatchNamespace batchNamespace : batchNamespaces) {
			// Note: use both pointer equality and equals.
			if (batchNamespace.namespace == ns || batchNamespace.namespace.equals(ns)) {
				return batchNamespace;
			}
		}
		return null;
	}

	private static BatchNode findBatchNode(List<BatchNode> nodes, Node node) {
		for (BatchNode batchNode : nodes) {
			// Note: using pointer equality for performance.
			if (batchNode.node == node) {
				return batchNode;
			}
		}
		return null;
	}

	public static final class BatchNamespace {
		public final String namespace;
		public int[] offsets;
		public int offsetsSize;

		public BatchNamespace(String namespace, int capacity, int offset) {
			this.namespace = namespace;
			this.offsets = new int[capacity];
			this.offsets[0] = offset;
			this.offsetsSize = 1;
		}
		
		public void add(int offset) {
			if (offsetsSize >= offsets.length) {
				int[] copy = new int[offsetsSize * 2];	        
				System.arraycopy(offsets, 0, copy, 0, offsetsSize);
				offsets = copy;
			}
			offsets[offsetsSize++] = offset;
		}
	}	
}
