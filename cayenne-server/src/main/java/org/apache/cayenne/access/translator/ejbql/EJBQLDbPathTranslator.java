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
package org.apache.cayenne.access.translator.ejbql;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.cayenne.ejbql.EJBQLBaseVisitor;
import org.apache.cayenne.ejbql.EJBQLException;
import org.apache.cayenne.ejbql.EJBQLExpression;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.relationship.DbRelationship;
import org.apache.cayenne.map.Relationship;
import org.apache.cayenne.map.relationship.DirectionalJoinVisitor;
import org.apache.cayenne.reflect.ClassDescriptor;

public abstract class EJBQLDbPathTranslator extends EJBQLBaseVisitor {

	private EJBQLTranslationContext context;
	protected DbEntity currentEntity;
	private String lastPathComponent;
	protected String lastAlias;
	protected String idPath;
	protected String joinMarker;
	private String fullPath;
	private boolean usingAliases;

	public EJBQLDbPathTranslator(EJBQLTranslationContext context) {
		super(true);
		this.context = context;
		this.usingAliases = true;
	}

	protected abstract void appendMultiColumnPath(EJBQLMultiColumnOperand operand);

	@Override
	public boolean visitDbPath(EJBQLExpression expression, int finishedChildIndex) {
		if (finishedChildIndex > 0) {

			if (finishedChildIndex + 1 < expression.getChildrenCount()) {
				processIntermediatePathComponent();
			} else {
				processLastPathComponent();
			}
		}

		return true;
	}

	@Override
	public boolean visitIdentifier(EJBQLExpression expression) {

		// expression id is always rooted in an ObjEntity, even for DbPath...
		ClassDescriptor descriptor = context.getEntityDescriptor(expression.getText());
		if (descriptor == null) {
			throw new EJBQLException("Invalid identification variable: " + expression.getText());
		}

		this.currentEntity = descriptor.getEntity().getDbEntity();
		this.idPath = expression.getText();
		this.joinMarker = EJBQLJoinAppender.makeJoinTailMarker(idPath);
		this.fullPath = idPath;
		return true;
	}

	@Override
	public boolean visitIdentificationVariable(EJBQLExpression expression) {

		// TODO: andrus 6/11/2007 - if the path ends with relationship, the last
		// join will
		// get lost...
		if (lastPathComponent != null) {
			resolveJoin(true);
		}

		this.lastPathComponent = expression.getText();
		return true;
	}

	private void resolveJoin(boolean inner) {

		EJBQLJoinAppender joinAppender = context.getTranslatorFactory().getJoinAppender(context);

		// TODO: andrus 1/6/2007 - conflict with object path naming... maybe
		// 'registerReusableJoin' should normalize everything to a db path?
		String newPath = idPath + '.' + lastPathComponent;
		String oldPath = joinAppender.registerReusableJoin(idPath, lastPathComponent, newPath);

		this.fullPath = fullPath + '.' + lastPathComponent;

		if (oldPath != null) {
			this.idPath = oldPath;
			this.lastAlias = context.getTableAlias(oldPath,
					context.getQuotingStrategy().quotedFullyQualifiedName(currentEntity));
		} else {

			// register join
			if (inner) {
				joinAppender.appendInnerJoin(joinMarker, new EJBQLTableId(idPath), new EJBQLTableId(fullPath));
				this.lastAlias = context.getTableAlias(fullPath,
						context.getQuotingStrategy().quotedFullyQualifiedName(currentEntity));
			} else {
				joinAppender.appendOuterJoin(joinMarker, new EJBQLTableId(idPath), new EJBQLTableId(fullPath));

				Relationship lastRelationship = currentEntity.getRelationship(lastPathComponent);
				DbEntity targetEntity = (DbEntity) lastRelationship.getTargetEntity();

				this.lastAlias = context.getTableAlias(fullPath,
						context.getQuotingStrategy().quotedFullyQualifiedName(targetEntity));
			}

			this.idPath = newPath;
		}
	}

	private void processIntermediatePathComponent() {
		DbRelationship relationship = currentEntity.getRelationship(lastPathComponent);
		if (relationship == null) {
			throw new EJBQLException("Unknown relationship '" + lastPathComponent + "' for entity '"
					+ currentEntity.getName() + "'");
		}

		this.currentEntity = (DbEntity) relationship.getTargetEntity();
	}

	private void processLastPathComponent() {

		DbAttribute attribute = currentEntity.getAttribute(lastPathComponent);

		if (attribute != null) {
			processTerminatingAttribute(attribute);
			return;
		}

		DbRelationship relationship = currentEntity.getRelationship(lastPathComponent);
		if (relationship != null) {
			processTerminatingRelationship(relationship);
			return;
		}

		throw new IllegalStateException("Invalid path component: " + lastPathComponent);
	}

	protected void processTerminatingAttribute(DbAttribute attribute) {

		DbEntity table = (DbEntity) attribute.getEntity();

		if (isUsingAliases()) {
			String alias = this.lastAlias != null ? lastAlias : context.getTableAlias(idPath, context
					.getQuotingStrategy().quotedFullyQualifiedName(table));
			context.append(' ').append(alias).append('.').append(context.getQuotingStrategy().quotedName(attribute));
		} else {
			context.append(' ').append(context.getQuotingStrategy().quotedName(attribute));
		}
	}

	protected void processTerminatingRelationship(DbRelationship relationship) {

		if (relationship.isToMany()) {

			// use an outer join for to-many matches
			resolveJoin(false);

			DbEntity table = relationship.getTargetEntity();

			String alias = this.lastAlias != null ? lastAlias : context.getTableAlias(idPath, context
					.getQuotingStrategy().quotedFullyQualifiedName(table));

			Collection<DbAttribute> pks = table.getPrimaryKeys();

			if (pks.size() == 1) {
				DbAttribute pk = pks.iterator().next();
				context.append(' ');
				if (isUsingAliases()) {
					context.append(alias).append('.');
				}
				context.append(context.getQuotingStrategy().quotedName(pk));
			} else {
				throw new EJBQLException("Multi-column PK to-many matches are not yet supported.");
			}
		} else {
			// match FK against the target object

			DbEntity table = relationship.getSourceEntity();

			String alias = this.lastAlias != null ? lastAlias : context.getTableAlias(idPath, context
					.getQuotingStrategy().quotedFullyQualifiedName(table));

			relationship.accept(new DirectionalJoinVisitor<Void>() {
				@Override
				public Void visit(DbAttribute[] source, DbAttribute[] target) {
					int length = source.length;
					Map<String, String> multiColumnMatch = new HashMap<>(length + 2);
					for(int i = 0; i < length; i++) {
						String column = isUsingAliases() ? alias + "." + source[i].getName() : source[i].getName();
						multiColumnMatch.put(target[i].getName(), column);
					}
					appendMultiColumnPath(EJBQLMultiColumnOperand.getPathOperand(context, multiColumnMatch));
					return null;
				}

				@Override
				public Void visit(DbAttribute source, DbAttribute target) {
					context.append(' ');
					if (isUsingAliases()) {
						context.append(alias).append('.');
					}
					context.append(context.getQuotingStrategy().quotedName(source));
					return null;
				}
			});
		}
	}

	public boolean isUsingAliases() {
		return usingAliases;
	}

	public void setUsingAliases(boolean usingAliases) {
		this.usingAliases = usingAliases;
	}

	protected void resolveJoin() {

		EJBQLJoinAppender joinAppender = context.getTranslatorFactory().getJoinAppender(context);

		String newPath = idPath + '.' + lastPathComponent;
		String oldPath = joinAppender.registerReusableJoin(idPath, lastPathComponent, newPath);

		this.fullPath = fullPath + '.' + lastPathComponent;

		if (oldPath != null) {
			this.idPath = oldPath;
			DbRelationship lastRelationship = currentEntity.getRelationship(lastPathComponent);
			if (lastRelationship != null) {
				DbEntity targetEntity = lastRelationship.getTargetEntity();

				this.lastAlias = context.getTableAlias(fullPath,
						context.getQuotingStrategy().quotedFullyQualifiedName(targetEntity));
			} else {
				String tableName = context.getQuotingStrategy().quotedFullyQualifiedName(currentEntity);
				this.lastAlias = context.getTableAlias(oldPath, tableName);
			}
		} else {
			DbRelationship lastRelationship = currentEntity.getRelationship(lastPathComponent);

			DbEntity targetEntity = null;
			if (lastRelationship != null) {
				targetEntity = lastRelationship.getTargetEntity();
			} else {
				targetEntity = currentEntity;
			}

			// register join
			joinAppender.appendInnerJoin(joinMarker, new EJBQLTableId(idPath), new EJBQLTableId(fullPath));
			// TODO: outer joins handling

			this.lastAlias = context.getTableAlias(fullPath,
					context.getQuotingStrategy().quotedFullyQualifiedName(targetEntity));

			this.idPath = newPath;
		}
	}
}
