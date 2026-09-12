package com.Marketplace_Management.Shared.Utils.QueryBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.impl.DSL;

public class RelationshipBuilder {

	private final EntityMetadataRegistry registry;

	public RelationshipBuilder(EntityMetadataRegistry registry) {
		this.registry = registry;
	}

	public Table<?> resolveTargetTable(
			RelationshipMetadata relationship,
			String alias) {

		EntityMetadata metadata = registry.require(
				relationship.getTargetEntity());

		Table<?> table = DSL.table(
				DSL.name(metadata.getTableName()));

		return alias != null
				? table.as(alias)
				: table;
	}

	public Table<?> resolveJoinTable(RelationshipMetadata relationship) {
		if (relationship.getJoinTable() == null
				|| relationship.getJoinTable().isEmpty()) {
			return null;
		}

		return DSL.table(
				DSL.name(relationship.getJoinTable()));
	}

	public Field<?>[] resolveSelectedFields(QueryBuilder<?> query) {
		List<Field<?>> fields = new ArrayList<>();

		EntityMetadata metadata = query.getMetadata();

		if (query.getSelectedFields() != null) {
			for (Field<?> field : query.getSelectedFields()) {
				ColumnMetadata column = metadata.getColumns()
						.get(field.getName());

				if (column == null) {
					throw new IllegalArgumentException(
							"Unknown field: " + field.getName());
				}

				fields.add(DSL.field(
						DSL.name(
								resolveTableName(query),
								column.getColumnName())));
			}
		} else {
			// If no specific selection, select all fields from this entity
			String tableName = resolveTableName(query);
			for (ColumnMetadata column : metadata.getColumns().values()) {
				fields.add(DSL.field(
						DSL.name(tableName, column.getColumnName())));
			}
		}

		for (QueryBuilder<?> child : query.getChildren().values()) {
			fields.addAll(Arrays.asList(
					resolveSelectedFields(child)));
		}

		return fields.toArray(Field<?>[]::new);
	}

	public Condition buildJoinCondition(Table<?> sourceTable, Table<?> targetTable, EntityMetadata sourceMetadata, RelationshipMetadata relationship) {

		return switch (relationship.getType()) {
			case ONE_TO_ONE, MANY_TO_ONE ->
					buildDirectJoinCondition(
							sourceTable,
							targetTable,
							relationship);

			case ONE_TO_MANY ->
					buildOneToManyJoinCondition(
							sourceTable,
							targetTable,
							sourceMetadata,
							relationship);

			case MANY_TO_MANY ->
					throw new UnsupportedOperationException(
							"Many-to-many requires explicit join table handling.");
		};
	}

	private Condition buildDirectJoinCondition(Table<?> sourceTable, Table<?> targetTable, RelationshipMetadata relationship) {

		Field<?> sourceField = field(
				sourceTable,
				relationship.getJoinColumn());

		EntityMetadata targetMetadata = registry.require(
				relationship.getTargetEntity());

		Field<?> targetField = field(
				targetTable,
				getPrimaryKeyColumn(targetMetadata));

		return DSL.condition(
				"{0} = {1}",
				sourceField,
				targetField);
	}

	private Condition buildOneToManyJoinCondition(Table<?> sourceTable, Table<?> targetTable, EntityMetadata sourceMetadata, RelationshipMetadata relationship) {

		Field<?> sourceField = field(
				sourceTable,
				getPrimaryKeyColumn(sourceMetadata));

		Field<?> targetField = field(
				targetTable,
				relationship.getJoinColumn());

		return DSL.condition(
				"{0} = {1}",
				sourceField,
				targetField);
	}

	public Condition buildSourceToJoinCondition(Table<?> sourceTable, Table<?> joinTable, EntityMetadata sourceMetadata, RelationshipMetadata relationship) {

		Field<?> sourceField = field(
				sourceTable,
				getPrimaryKeyColumn(sourceMetadata));

		Field<?> joinField = field(
				joinTable,
				relationship.getJoinColumn());

		return DSL.condition(
				"{0} = {1}",
				sourceField,
				joinField);
	}

	public Condition buildJoinToTargetCondition(Table<?> joinTable, Table<?> targetTable, EntityMetadata targetMetadata, RelationshipMetadata relationship) {

		Field<?> joinField = field(
				joinTable,
				relationship.getInverseJoinColumn());

		Field<?> targetField = field(
				targetTable,
				getPrimaryKeyColumn(targetMetadata));

		return DSL.condition(
				"{0} = {1}",
				joinField,
				targetField);
	}

	public Field<?> field(Table<?> table, String columnName) {
		return DSL.field(
				DSL.name(
						table.getName(),
						columnName));
	}

	public String resolveTableName(QueryBuilder<?> query) {
		return query.getAlias() != null
				? query.getAlias()
				: query.getMetadata().getTableName();
	}

	public String getPrimaryKeyColumn(EntityMetadata metadata) {
		for (ColumnMetadata column : metadata.getColumns().values()) {
			if ("id".equals(column.getFieldName())
					|| "id".equals(column.getColumnName())) {
				return column.getColumnName();
			}
		}

		return "id";
	}
}