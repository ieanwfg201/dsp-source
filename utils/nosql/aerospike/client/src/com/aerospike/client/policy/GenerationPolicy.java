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
package com.aerospike.client.policy;

/**
 * How to handle record writes based on record generation.
 */
public enum GenerationPolicy {
	/**
	 * Do not use record generation to restrict writes.
	 */
	NONE,
	
	/**
	 * Update/delete record if expected generation is equal to server generation. Otherwise, fail. 
	 */
	EXPECT_GEN_EQUAL,

	/**
	 * Update/delete record if expected generation greater than the server generation. Otherwise, fail.
	 * This is useful for restore after backup. 
	 */
	EXPECT_GEN_GT
}
