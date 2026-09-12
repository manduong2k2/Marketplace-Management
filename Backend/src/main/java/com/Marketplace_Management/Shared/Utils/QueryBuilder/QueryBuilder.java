package com.Marketplace_Management.Shared.Utils.QueryBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.JoinType;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.SelectQuery;
import org.jooq.SortField;
import org.jooq.SortOrder;
import org.jooq.Table;
import org.jooq.conf.ParamType;
import org.jooq.impl.DSL;

import lombok.Data;
import tools.jackson.databind.ObjectMapper;

@Data
public class QueryBuilder<E> {

	private final DSLContext dsl;
	private final EntityMetadataRegistry registry;
	private final RelationshipBuilder relationships;
	private EntityMetadata metadata;
	private String alias;
	private String path;
	private Field<?>[] selectedFields;
	private Condition condition;
	private final ObjectMapper objectMapper;
	private boolean countMode;
	private boolean withDeleted = false;

	private final List<SortField<?>> orderFields = new ArrayList<>();
	private final List<Field<?>> orderedFields = new ArrayList<>();

	private final Map<String, QueryBuilder<?>> children = new LinkedHashMap<>();

	public QueryBuilder(DSLContext dsl, EntityMetadataRegistry registry, ObjectMapper objectMapper) {
		this.dsl = dsl;
		this.registry = registry;
		this.objectMapper = objectMapper;
		this.relationships = new RelationshipBuilder(registry);
	}

	// =========================================================
	// QUERY
	// =========================================================

	public QueryBuilder<E> query(Class<?> entityClass) {
		metadata = registry.require(entityClass);

		if (path == null) {
			path = metadata.getTableName();
		}

		return this;
	}

	public QueryBuilder<E> alias(String alias) {
		this.alias = alias;
		return this;
	}

	QueryBuilder<E> path(String path) {
		this.path = path;
		return this;
	}

	public String getRawSql() {
		return DSL.using(SQLDialect.POSTGRES)
				.parser()
				.parseQuery(this.build().getSQL(ParamType.INLINED))
				.getSQL(ParamType.INLINED)
				.replaceAll("\"", "");
	}

	public long count() {
		SelectQuery<?> query = dsl.selectQuery();

		Table<?> rootTable = resolveTable();

		query.addSelect(DSL.countDistinct(resolveField("id")).as("total"));

		query.addFrom(rootTable);

		addJoins(query, rootTable, metadata, this);

		if (condition != null) {
			query.addConditions(condition);
		}

		query.addConditions(softDeleteCondition());

		Long total = query.fetchOne("total", Long.class);

		return total != null ? total : 0L;
	}

	public QueryBuilder<E> withDeleted() {
		this.withDeleted = true;
		return this;
	}

	String getEffectiveAlias() {
		if (alias != null && !alias.isBlank()) {
			return sanitizeAlias(alias);
		}

		if (path != null && !path.isBlank()) {
			return sanitizeAlias(path);
		}

		return sanitizeAlias(metadata.getTableName());
	}

	private String sanitizeAlias(String value) {
		return value.replace(".", "_")
				.replace("-", "_")
				.replace(" ", "_");
	}

	// =========================================================
	// SELECT
	// =========================================================

	public QueryBuilder<E> select(String... fields) {
		selectedFields = Arrays.stream(fields)
				.map(this::resolveField)
				.toArray(Field<?>[]::new);

		return this;
	}

	public QueryBuilder<E> select(Field<?>... fields) {
		selectedFields = fields;
		return this;
	}

	// =========================================================
	// WHERE
	// =========================================================

	public QueryBuilder<E> where(Condition condition) {
		this.condition = this.condition == null
				? condition
				: this.condition.and(condition);

		return this;
	}

	public QueryBuilder<E> where(String field, String operator, Object value) {
		return where(buildCondition(field, operator, value));
	}

	public QueryBuilder<E> orWhere(Condition condition) {
		this.condition = this.condition == null
				? condition
				: this.condition.or(condition);

		return this;
	}

	public QueryBuilder<E> orWhere(String field, String operator, Object value) {
		return orWhere(buildCondition(field, operator, value));
	}

	// =========================================================
	// ORDER BY
	// =========================================================

	public QueryBuilder<E> orderBy(String field, String direction) {

		SortOrder order = switch (direction.toUpperCase()) {
			case "ASC" -> SortOrder.ASC;
			case "DESC" -> SortOrder.DESC;
			default -> throw new IllegalArgumentException(
					"Unsupported sort direction: " + direction);
		};

		Field<?> resolvedField = resolveField(field);

		orderedFields.add(resolvedField);
		orderFields.add(resolvedField.sort(order));

		return this;
	}

	public QueryBuilder<E> orderBy(SortField<?>... fields) {
		orderFields.addAll(Arrays.asList(fields));
		return this;
	}

	// =========================================================
	// RELATIONSHIPS
	// =========================================================

	public QueryBuilder<E> with(String relationshipName, Consumer<QueryBuilder<?>> callback) {
		if (metadata == null) {
			throw new IllegalStateException("Call query() before with()");
		}

		RelationshipMetadata relationship = metadata.getRelationships().get(relationshipName);

		if (relationship == null) {
			throw new IllegalArgumentException("Unknown relationship: " + relationshipName);
		}

		String childPath = buildChildPath(relationshipName);

		QueryBuilder<?> child = new QueryBuilder<>(dsl, registry, objectMapper)
				.query(relationship.getTargetEntity())
				.path(childPath);

		callback.accept(child);
		children.put(relationshipName, child);

		return this;
	}

	public QueryBuilder<E> when(boolean condition, Consumer<QueryBuilder<E>> callback) {
		if (condition) {
			callback.accept(this);
		}

		return this;
	}

	private String buildChildPath(String relationshipName) {
		return getEffectiveAlias() + "_" + relationshipName;
	}

	public SelectQuery<?> build() {
		if (metadata == null) {
			throw new IllegalStateException("Entity is not specified. Call query() first.");
		}

		SelectQuery<?> query = dsl.selectQuery();
		Table<?> rootTable = resolveTable();

		if (countMode) {
			query.addSelect(
					DSL.countDistinct(
							resolveField("id")).as("total"));

			query.addFrom(rootTable);
			addJoins(query, rootTable, metadata, this);

			if (condition != null) {
				query.addConditions(condition);
			}

			query.addConditions(softDeleteCondition());

			return query;
		}

		addSelect(query);
		query.addFrom(rootTable);
		addJoins(query, rootTable, metadata, this);

		if (condition != null) {
			query.addConditions(condition);
		}

		query.addConditions(softDeleteCondition());

		if (!orderFields.isEmpty()) {
			query.addOrderBy(orderFields);
		}

		return query;
	}

	// =========================================================
	// EXECUTION
	// =========================================================

	public Result<?> execute() {
		return build().fetch();
	}

	public Result<?> execute(int limit, int offset) {
		SelectQuery<?> query = build();
		query.addLimit(limit);
		query.addOffset(offset);
		return query.fetch();
	}

	public List<Map<String, Object>> get() {
		return structureResult(execute());
	}

	public List<Map<String, Object>> get(int limit, int offset) {

		List<?> ids = fetchPageIds(limit, offset);

		if (ids.isEmpty()) {
			return List.of();
		}

		Condition pageCondition = resolveField("id").in(ids);

		SelectQuery<?> query = build();
		query.addConditions(pageCondition);

		return structureResult(query.fetch());
	}

	public <T> T to(Map<String, Object> data, Class<T> clazz) {
		return objectMapper.convertValue(data, clazz);
	}

	private List<?> fetchPageIds(int limit, int offset) {

		SelectQuery<?> query = dsl.selectQuery();

		Table<?> rootTable = resolveTable();

		query.addSelect(resolveField("id"));
		for (Field<?> field : orderedFields) {
			query.addSelect(field);
		}
		query.setDistinct(true);

		query.addFrom(rootTable);

		addJoins(query, rootTable, metadata, this);

		if (condition != null) {
			query.addConditions(condition);
		}

		query.addConditions(softDeleteCondition());

		if (!orderFields.isEmpty()) {
			query.addOrderBy(orderFields);
		}

		query.addLimit(limit);
		query.addOffset(offset);

		return query.fetch(resolveField("id"));
	}

	private void addSelect(SelectQuery<?> query) {
		if (countMode) {
			query.addSelect(
					resolveField("id").as(
							getEffectiveAlias() + "_" + resolvePrimaryKeyColumn(metadata)));
			return;
		}

		List<Field<?>> fields = new ArrayList<>();
		addNodeSelectFields(fields, this);

		if (fields.isEmpty()) {
			query.addSelect(DSL.field("*"));
		} else {
			query.addSelect(fields);
		}
	}

	private void addNodeSelectFields(List<Field<?>> fields, QueryBuilder<?> node) {

		String nodeAlias = node.getEffectiveAlias();

		if (node.selectedFields != null && node.selectedFields.length > 0) {

			for (Field<?> selectedField : node.selectedFields) {

				String columnName = selectedField.getName();

				ColumnMetadata column = node.metadata.getColumns()
						.values()
						.stream()
						.filter(c -> c.getColumnName().equals(columnName))
						.findFirst()
						.orElseThrow(() -> new IllegalArgumentException(
								"Unknown column: " + columnName
										+ " for entity " + node.metadata.getTableName()));

				fields.add(DSL.field(DSL.name(nodeAlias, column.getColumnName()))
						.as(nodeAlias + "_" + column.getColumnName()));
			}

		} else {

			for (ColumnMetadata column : node.metadata.getColumns().values()) {

				String columnName = column.getColumnName();

				fields.add(DSL.field(DSL.name(nodeAlias, columnName)).as(nodeAlias + "_" + columnName));
			}
		}

		for (QueryBuilder<?> child : node.children.values()) {
			addNodeSelectFields(fields, child);
		}
	}

	// =========================================================
	// JOINS
	// =========================================================

	private void addJoins(SelectQuery<?> query, Table<?> sourceTable, EntityMetadata sourceMetadata,
			QueryBuilder<?> parent) {

		for (Map.Entry<String, QueryBuilder<?>> entry : parent.children.entrySet()) {
			String relationshipName = entry.getKey();
			QueryBuilder<?> child = entry.getValue();

			RelationshipMetadata relationship = sourceMetadata
					.getRelationships()
					.get(relationshipName);

			if (relationship == null) {
				throw new IllegalArgumentException(
						"Unknown relationship: " + relationshipName);
			}

			addJoin(query, sourceTable, sourceMetadata, child, relationship);
		}
	}

	private void addJoin(SelectQuery<?> query, Table<?> sourceTable, EntityMetadata sourceMetadata,
			QueryBuilder<?> child, RelationshipMetadata relationship) {

		Table<?> targetTable = relationships.resolveTargetTable(
				relationship,
				child.getEffectiveAlias());

		switch (relationship.getType()) {
			case ONE_TO_ONE, MANY_TO_ONE, ONE_TO_MANY -> {
				Condition joinCondition = relationships.buildJoinCondition(
						sourceTable,
						targetTable,
						sourceMetadata,
						relationship);

				if (child.condition != null) {
					joinCondition = joinCondition.and(child.condition);
				}

				joinCondition = joinCondition.and(child.softDeleteCondition());

				query.addJoin(targetTable, JoinType.LEFT_OUTER_JOIN, joinCondition);

				addJoins(query, targetTable, child.metadata, child);
			}

			case MANY_TO_MANY -> {
				Table<?> joinTable = relationships.resolveJoinTable(relationship);

				if (joinTable == null) {
					throw new IllegalArgumentException(
							"Many-to-many relationship requires join table: "
									+ relationship.getFieldName());
				}

				Condition sourceToJoin = relationships.buildSourceToJoinCondition(
						sourceTable,
						joinTable,
						sourceMetadata,
						relationship);

				query.addJoin(joinTable, JoinType.LEFT_OUTER_JOIN, sourceToJoin);

				EntityMetadata targetMetadata = registry.require(
						relationship.getTargetEntity());

				Condition joinToTarget = relationships.buildJoinToTargetCondition(
						joinTable,
						targetTable,
						targetMetadata,
						relationship);

				if (child.condition != null) {
					joinToTarget = joinToTarget.and(child.condition);
				}

				joinToTarget = joinToTarget.and(child.softDeleteCondition());

				query.addJoin(targetTable, JoinType.LEFT_OUTER_JOIN, joinToTarget);

				addJoins(query, targetTable, targetMetadata, child);
			}
		}
	}

	// =========================================================
	// CONDITION
	// =========================================================

	private Condition buildCondition(String fieldName, String operator, Object value) {
		Field<?> field = resolveField(fieldName);

		return switch (operator.toUpperCase()) {
			case "=" -> DSL.condition("{0} = {1}", field, value);
			case "!=" -> DSL.condition("{0} <> {1}", field, value);
			case ">" -> DSL.condition("{0} > {1}", field, value);
			case ">=" -> DSL.condition("{0} >= {1}", field, value);
			case "<" -> DSL.condition("{0} < {1}", field, value);
			case "<=" -> DSL.condition("{0} <= {1}", field, value);
			case "LIKE" -> field.like(value.toString());
			case "ILIKE" -> field.likeIgnoreCase(value.toString());
			case "NOT LIKE" -> field.notLike(value.toString());
			case "NOT ILIKE" -> field.notLikeIgnoreCase(value.toString());
			case "IN" -> field.in((Collection<?>) value);
			case "NOT IN" -> field.notIn((Collection<?>) value);
			case "IS NULL" -> field.isNull();
			case "IS NOT NULL" -> field.isNotNull();
			default -> throw new IllegalArgumentException(
					"Unsupported operator: " + operator);
		};
	}

	private Condition softDeleteCondition() {
		if (withDeleted || !metadata.hasSoftDelete()) {
			return DSL.noCondition();
		}

		ColumnMetadata deletedAt = metadata.getColumns().get("deleted_at");

		if (deletedAt == null) {
			return DSL.noCondition();
		}

		return DSL.field(
				DSL.name(
						getEffectiveAlias(),
						deletedAt.getColumnName()))
				.isNull();
	}

	// =========================================================
	// FIELD RESOLUTION
	// =========================================================

	private Field<?> resolveField(String fieldName) {
		if (fieldName.contains(".")) {
			String[] parts = fieldName.split("\\.", 2);
			String relationshipName = parts[0];
			String targetField = parts[1];

			QueryBuilder<?> child = children.get(relationshipName);

			if (child == null) {
				throw new IllegalArgumentException(
						"Unknown relationship: " + relationshipName);
			}

			return child.resolveField(targetField);
		}

		ColumnMetadata column = metadata.getColumns().get(fieldName);

		if (column == null) {
			throw new IllegalArgumentException(
					"Unknown field: " + fieldName
							+ " for entity " + metadata.getTableName());
		}

		return DSL.field(
				DSL.name(getEffectiveAlias(), column.getColumnName()));
	}

	// =========================================================
	// TABLE
	// =========================================================

	private Table<?> resolveTable() {
		Table<?> table = DSL.table(DSL.name(metadata.getTableName()));
		return table.as(getEffectiveAlias());
	}

	// =========================================================
	// RESULT STRUCTURING
	// =========================================================

	private List<Map<String, Object>> structureResult(Result<?> result) {
		Map<Object, Map<String, Object>> grouped = new LinkedHashMap<>();

		String rootAlias = getEffectiveAlias();
		String rootPrimaryKey = resolvePrimaryKeyColumn(metadata);

		try {
			for (org.jooq.Record record : result) {
				String rootIdField = rootAlias + "_" + rootPrimaryKey;
				Object rootId = record.get(rootIdField);

				if (rootId == null) {
					continue;
				}

				Map<String, Object> entity = grouped.computeIfAbsent(
						rootId,
						k -> new LinkedHashMap<>());

				extractNodeFields(record, this, entity);
				extractChildren(record, this, entity);
			}
		} catch (Exception e) {
			System.err.println("Error in structureResult: " + e.getMessage());
			throw new RuntimeException("Failed to structure result", e);
		}

		return new ArrayList<>(grouped.values());
	}

	private void extractNodeFields(org.jooq.Record record, QueryBuilder<?> node, Map<String, Object> target) {
		String nodeAlias = node.getEffectiveAlias();

		if (node.selectedFields != null && node.selectedFields.length > 0) {

			for (Field<?> field : node.selectedFields) {

				String columnName = field.getName();

				ColumnMetadata column = node.metadata.getColumns()
						.values()
						.stream()
						.filter(c -> c.getColumnName().equals(columnName))
						.findFirst()
						.orElseThrow(() -> new IllegalArgumentException(
								"Unknown column: " + columnName
										+ " for entity " + node.metadata.getTableName()));

				String fieldName = column.getFieldName();

				String fieldAlias = nodeAlias + "_" + columnName;

				Object value = record.get(fieldAlias);

				if (value != null) {
					target.put(fieldName, value);
				}
			}

		} else {

			for (ColumnMetadata column : node.metadata.getColumns().values()) {

				String fieldName = column.getFieldName();
				String columnName = column.getColumnName();

				Object value = record.get(nodeAlias + "_" + columnName);

				if (value != null) {
					target.put(fieldName, value);
				}
			}
		}
	}

	private void extractChildren(org.jooq.Record record, QueryBuilder<?> parent, Map<String, Object> parentData) {

		for (Map.Entry<String, QueryBuilder<?>> entry : parent.children.entrySet()) {
			String relationshipName = entry.getKey();
			QueryBuilder<?> child = entry.getValue();

			RelationshipMetadata relationship = parent.metadata
					.getRelationships()
					.get(relationshipName);

			if (relationship == null) {
				continue;
			}

			Map<String, Object> childData = new LinkedHashMap<>();

			extractNodeFields(record, child, childData);

			if (isEmptyEntity(childData)) {
				continue;
			}

			extractChildren(record, child, childData);

			if (relationship.getType() == RelationshipType.ONE_TO_MANY
					|| relationship.getType() == RelationshipType.MANY_TO_MANY) {

				@SuppressWarnings("unchecked")
				List<Map<String, Object>> relatedList = (List<Map<String, Object>>) parentData
						.computeIfAbsent(relationshipName, k -> new ArrayList<>());

				Object childId = childData.get(resolvePrimaryKeyFieldName(child.metadata));

				if (!containsEntity(relatedList, childId)) {
					relatedList.add(childData);
				}
			} else {
				parentData.put(relationshipName, childData);
			}
		}
	}

	private boolean isEmptyEntity(Map<String, Object> entity) {
		return entity == null || entity.isEmpty();
	}

	private boolean containsEntity(List<Map<String, Object>> entities, Object id) {
		if (id == null) {
			return false;
		}

		for (Map<String, Object> entity : entities) {
			Object existingId = entity.get("id");

			if (id.equals(existingId)) {
				return true;
			}
		}

		return false;
	}

	private String resolvePrimaryKeyColumn(EntityMetadata entityMetadata) {
		ColumnMetadata id = entityMetadata.getColumns().get("id");

		if (id == null) {
			throw new IllegalStateException(
					"Entity " + entityMetadata.getTableName()
							+ " does not contain an 'id' field.");
		}

		return id.getColumnName();
	}

	private String resolvePrimaryKeyFieldName(EntityMetadata entityMetadata) {
		ColumnMetadata id = entityMetadata.getColumns().get("id");
		return id != null ? id.getFieldName() : "id";
	}
}