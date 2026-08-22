package com.Marketplace_Management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.Marketplace_Management.Catalog.Entities.ProductEntity;
import com.Marketplace_Management.Catalog.Entities.ProductVariantEntity;
import com.Marketplace_Management.Catalog.Entities.BrandEntity;
import com.Marketplace_Management.Catalog.Entities.CategoryEntity;
import com.Marketplace_Management.Shared.Entities.FileEntity;
import com.Marketplace_Management.Shared.Repositories.QueryBuilder.QueryBuilder;

import java.util.List;

@SpringBootApplication
public class Marketplace_ManagementApplication {
	public static void main(String[] args) {
		SpringApplication.run(Marketplace_ManagementApplication.class, args);

		//String query = QueryBuilder.query(ProductEntity.class)
		//		.select("id", "name")
		//		.with(List.of(
		//				QueryBuilder.child(BrandEntity.class, "brand").select("id", "name"),
		//				QueryBuilder.child(ProductVariantEntity.class, "variants")
		//					.select("id", "name", "product_id")
		//					.with(List.of(QueryBuilder.child(FileEntity.class, "files"))),
		//				QueryBuilder.child(CategoryEntity.class, "categories").select("id", "name")))
		//		.build();
//
		//System.out.println("========== QUERY ==========");
		//System.out.println(query);
		//System.out.println("========== END ==========");
	}
}

