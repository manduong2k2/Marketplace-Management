package com.Marketplace_Management.Shared.Utils.QueryBuilder;

import java.util.List;

import org.jooq.Record;
import org.jooq.Result;

public interface QueryMapper {

    <R> List<R> map(
            Result<? extends Record> result,
            Class<R> responseType
    );

    <R> R map(
            Record record,
            Class<R> responseType
    );
}
