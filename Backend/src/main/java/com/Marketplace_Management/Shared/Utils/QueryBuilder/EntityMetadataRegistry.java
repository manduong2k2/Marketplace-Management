package com.Marketplace_Management.Shared.Utils.QueryBuilder;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class EntityMetadataRegistry {

	private final Map<Class<?>, EntityMetadata> metadataMap = new HashMap<>();

	public void register(EntityMetadata metadata) {
		metadataMap.put(
				metadata.getEntityClass(),
				metadata);
	}

	public EntityMetadata get(Class<?> entityClass) {
		return metadataMap.get(entityClass);
	}

	public EntityMetadata require(Class<?> entityClass) {
		EntityMetadata metadata = metadataMap.get(entityClass);

		if (metadata == null) {
			throw new IllegalArgumentException(
					"No metadata found for: " + entityClass.getName());
		}

		return metadata;
	}
}