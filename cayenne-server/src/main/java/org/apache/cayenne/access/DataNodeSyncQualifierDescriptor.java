/*****************************************************************
 *   Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 ****************************************************************/

package org.apache.cayenne.access;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.ObjectId;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.relationship.DbRelationship;
import org.apache.cayenne.map.ObjAttribute;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.map.ObjRelationship;
import org.apache.cayenne.map.relationship.DirectionalJoinVisitor;

/**
 * Builds update qualifier snapshots, including optimistic locking.
 * 
 * @since 1.2
 * @deprecated since 4.2 as part of deprecated {@link LegacyDataDomainFlushAction}
 */
@Deprecated
class DataNodeSyncQualifierDescriptor {

	private List<DbAttribute> attributes;
	private List<Function<ObjectDiff, Object>> valueTransformers;
	private boolean usingOptimisticLocking;

	public boolean isUsingOptimisticLocking() {
		return usingOptimisticLocking;
	}

	List<DbAttribute> getAttributes() {
		return attributes;
	}

	Map<String, Object> createQualifierSnapshot(ObjectDiff diff) {
		int len = attributes.size();

		Map<String, Object> map = new HashMap<>(len * 2);
		for (int i = 0; i < len; i++) {
			DbAttribute attribute = attributes.get(i);
			if (!map.containsKey(attribute.getName())) {
				Object value = valueTransformers.get(i).apply(diff);
				map.put(attribute.getName(), value);
			}
		}

		return map;
	}

	void reset(DbEntityClassDescriptor descriptor) {

		attributes = new ArrayList<>(3);
		valueTransformers = new ArrayList<>(3);
		usingOptimisticLocking = descriptor.getEntity().getLockType() == ObjEntity.LOCK_TYPE_OPTIMISTIC;

		// master PK columns
		if (descriptor.isMaster()) {
			for (final DbAttribute attribute : descriptor.getDbEntity().getPrimaryKeys()) {
				attributes.add(attribute);
				valueTransformers.add(input -> {
                    ObjectId id = (ObjectId) input.getNodeId();
                    return id.getIdSnapshot().get(attribute.getName());
                });
			}
		} else {

			// TODO: andrus 12/23/2007 - only one step relationship is supported...
			if (descriptor.getPathFromMaster().size() != 1) {
				throw new CayenneRuntimeException(
				        "Only single step dependent relationships are currently supported. Actual path length: %d"
                        , descriptor.getPathFromMaster().size());
			}

			DbRelationship masterDependentDbRel = descriptor.getPathFromMaster().get(0);

			if (masterDependentDbRel != null) {
				masterDependentDbRel.accept(new DirectionalJoinVisitor<Void>() {

					private void build(DbAttribute target) {
						if (!attributes.contains(target)) {

							attributes.add(target);
							valueTransformers.add(input -> {
								ObjectId id = (ObjectId) input.getNodeId();
								return id.getIdSnapshot().get(target.getName());
							});
						}
					}

					@Override
					public Void visit(DbAttribute[] source, DbAttribute[] target) {
						for(DbAttribute attr : target) {
							build(attr);
						}
						return null;
					}

					@Override
					public Void visit(DbAttribute source, DbAttribute target) {
						build(target);
						return null;
					}
				});
            }
        }

		if (descriptor.isMaster() && usingOptimisticLocking) {

			for (final ObjAttribute attribute : descriptor.getEntity().getAttributes()) {

				if (attribute.isUsedForLocking() && !attribute.isFlattened()) {
					// only care about first step in a flattened attribute
					DbAttribute dbAttribute = (DbAttribute) attribute.getDbPathIterator().next();

					// only use qualifier if dbEntities match
					if (dbAttribute.getEntity().equals(descriptor.getDbEntity()) && !attributes.contains(dbAttribute)) {
						attributes.add(dbAttribute);
						valueTransformers.add(input -> input.getSnapshotValue(attribute.getName()));
					}
				}
			}

			for (final ObjRelationship relationship : descriptor.getEntity().getRelationships()) {

				if (relationship.isUsedForLocking()) {
					// only care about the first DbRelationship
					DbRelationship dbRelationship = relationship.getDbRelationships().get(0);

					dbRelationship.accept(new DirectionalJoinVisitor<Void>() {

						private void build(DbAttribute source, DbAttribute target) {

							// relationship transformers override attribute transformers for meaningful FK's...
							// why meaningful FKs can go out of sync is another story (CAY-595)
							int index = attributes.indexOf(source);
							if (index >= 0 && !source.isForeignKey()) {
								return;
							}

							Function<ObjectDiff, Object> transformer = input -> {
								ObjectId targetId = input.getArcSnapshotValue(relationship.getName());
								return targetId != null ? targetId.getIdSnapshot().get(target.getName()) : null;
							};

							if (index < 0) {
								attributes.add(source);
								valueTransformers.add(transformer);
							} else {
								valueTransformers.set(index, transformer);
							}
						}

						@Override
						public Void visit(DbAttribute[] source, DbAttribute[] target) {
							int length = source.length;
							for(int i = 0; i < length; i++) {
								build(source[i], target[i]);
							}
							return null;
						}

						@Override
						public Void visit(DbAttribute source, DbAttribute target) {
							build(source, target);
							return null;
						}
					});
				}
			}
		}
	}
}
