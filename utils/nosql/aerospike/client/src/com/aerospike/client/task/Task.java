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
package com.aerospike.client.task;

import com.aerospike.client.AerospikeException;
import com.aerospike.client.cluster.Cluster;
import com.aerospike.client.util.Util;

/**
 * Task used to poll for server task completion.
 */
public abstract class Task {
	protected final Cluster cluster;
	private boolean done;

	/**
	 * Initialize task with fields needed to query server nodes.
	 */
	public Task(Cluster cluster, boolean done) {
		this.cluster = cluster;
		this.done = done;
	}

	/**
	 * Wait for asynchronous task to complete using default sleep interval.
	 */
	public final void waitTillComplete() throws AerospikeException {
		waitTillComplete(1000);
	}

	/**
	 * Wait for asynchronous task to complete using given sleep interval.
	 */
	public final void waitTillComplete(int sleepInterval) throws AerospikeException {
		while (! done) {
			Util.sleep(sleepInterval);
			done = queryIfDone();
		}
	}

	/**
	 * Has task completed.
	 */
	public final boolean isDone() throws AerospikeException {
		if (done) {
			return true;
		}
		done = queryIfDone();
		return done;
	}
	
	/**
	 * Query all nodes for task completion status.
	 */
	protected abstract boolean queryIfDone() throws AerospikeException;
}
