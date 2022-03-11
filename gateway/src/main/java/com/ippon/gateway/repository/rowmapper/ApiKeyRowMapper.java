package com.ippon.gateway.repository.rowmapper;

import com.ippon.gateway.domain.ApiKey;
import com.ippon.gateway.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link ApiKey}, with proper type conversions.
 */
@Service
public class ApiKeyRowMapper implements BiFunction<Row, String, ApiKey> {

    private final ColumnConverter converter;

    public ApiKeyRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link ApiKey} stored in the database.
     */
    @Override
    public ApiKey apply(Row row, String prefix) {
        ApiKey entity = new ApiKey();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setClientId(converter.fromRow(row, prefix + "_client_id", String.class));
        return entity;
    }
}
