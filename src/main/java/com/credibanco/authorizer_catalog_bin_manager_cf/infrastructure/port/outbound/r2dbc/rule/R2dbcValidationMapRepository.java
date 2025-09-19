package com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.r2dbc.rule;

import com.credibanco.authorizer_catalog_bin_manager_cf.application.rule.port.outbound.ValidationMapRepository;
import com.credibanco.authorizer_catalog_bin_manager_cf.domain.rule.Validation;
import com.credibanco.authorizer_catalog_bin_manager_cf.domain.rule.ValidationDataType;
import com.credibanco.authorizer_catalog_bin_manager_cf.domain.rule.ValidationMap;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.function.BiFunction;

@Repository
public class R2dbcValidationMapRepository implements ValidationMapRepository {

    private static final ZoneId ZONE = ZoneId.systemDefault();
    private final DatabaseClient db;
    public R2dbcValidationMapRepository(DatabaseClient db) { this.db = db; }

    private static OffsetDateTime toOffset(Row r, String col) {
        LocalDateTime ldt = r.get(col, LocalDateTime.class);
        return ldt != null ? ldt.atZone(ZONE).toOffsetDateTime() : r.get(col, OffsetDateTime.class);
    }

    private static final BiFunction<Row, RowMetadata, ValidationMap> MAP_MAPPER = (r,m) ->
            ValidationMap.rehydrate(
                    r.get("subtype_val_map_id", Long.class),
                    r.get("subtype_code", String.class),
                    r.get("bin_efectivo", String.class),
                    r.get("validation_id", Long.class),
                    r.get("priority", Integer.class),
                    r.get("status", String.class),
                    toOffset(r,"valid_from"),
                    toOffset(r,"valid_to"),
                    toOffset(r,"created_at"),
                    toOffset(r,"updated_at"),
                    r.get("updated_by", String.class)
            );

    @Override
    public Mono<Boolean> existsActive(String subtypeCode, String binEfectivo, Long validationId) {
        return db.sql("""
                SELECT 1 FROM SUBTYPE_VALIDATION_MAP
                 WHERE SUBTYPE_CODE=:sc AND BIN_EFECTIVO=:be AND VALIDATION_ID=:vid AND STATUS='A'
                 FETCH FIRST 1 ROWS ONLY
                """).bind("sc", subtypeCode).bind("be", binEfectivo).bind("vid", validationId)
                .fetch().first().map(m->true).defaultIfEmpty(false);
    }

    @Override
    public Mono<ValidationMap> save(ValidationMap map) {
        var spec = db.sql("""
            MERGE INTO SUBTYPE_VALIDATION_MAP t
            USING (SELECT :sc SUBTYPE_CODE, :be BIN_EFECTIVO, :vid VALIDATION_ID FROM DUAL) s
               ON (t.SUBTYPE_CODE=s.SUBTYPE_CODE AND t.BIN_EFECTIVO=s.BIN_EFECTIVO AND t.VALIDATION_ID=s.VALIDATION_ID)
            WHEN MATCHED THEN UPDATE SET
                 t.PRIORITY=:p, t.STATUS=:st, t.VALID_FROM=:vf, t.VALID_TO=:vt, t.UPDATED_AT=SYSTIMESTAMP, t.UPDATED_BY=:by
            WHEN NOT MATCHED THEN INSERT
                 (SUBTYPE_VAL_MAP_ID, SUBTYPE_CODE, BIN_EFECTIVO, VALIDATION_ID, PRIORITY, STATUS, VALID_FROM, VALID_TO, CREATED_AT, UPDATED_AT, UPDATED_BY)
            VALUES (SEQ_SUBTYPE_VAL_MAP_ID.NEXTVAL, :sc, :be, :vid, :p, :st, :vf, :vt, SYSTIMESTAMP, SYSTIMESTAMP, :by)
            """)
                .bind("sc", map.subtypeCode())
                .bind("be", map.binEfectivo())
                .bind("vid", map.validationId())
                .bind("p",  map.priority())
                .bind("st", map.status())
                .bind("by", map.updatedBy());

        spec = (map.validFrom()!=null) ? spec.bind("vf", map.validFrom()) : spec.bindNull("vf", OffsetDateTime.class);
        spec = (map.validTo()!=null)   ? spec.bind("vt", map.validTo())   : spec.bindNull("vt", OffsetDateTime.class);

        return spec.fetch().rowsUpdated().then(findByNaturalKey(map.subtypeCode(), map.binEfectivo(), map.validationId()));
    }

    @Override
    public Mono<ValidationMap> findByNaturalKey(String subtypeCode, String binEfectivo, Long validationId) {
        return db.sql("""
                SELECT SUBTYPE_VAL_MAP_ID, SUBTYPE_CODE, BIN_EFECTIVO, VALIDATION_ID, PRIORITY, STATUS,
                       VALID_FROM, VALID_TO, CREATED_AT, UPDATED_AT, UPDATED_BY
                  FROM SUBTYPE_VALIDATION_MAP
                 WHERE SUBTYPE_CODE=:sc AND BIN_EFECTIVO=:be AND VALIDATION_ID=:vid
                """).bind("sc", subtypeCode).bind("be", binEfectivo).bind("vid", validationId)
                .map(MAP_MAPPER).one();
    }

    @Override
    public Flux<ValidationMap> findAll(String subtypeCode, String binEfectivo, String status, int page, int size) {
        int p=Math.max(0,page), s=Math.max(1,size), off=p*s;
        var sb=new StringBuilder("""
                SELECT SUBTYPE_VAL_MAP_ID, SUBTYPE_CODE, BIN_EFECTIVO, VALIDATION_ID, PRIORITY, STATUS,
                       VALID_FROM, VALID_TO, CREATED_AT, UPDATED_AT, UPDATED_BY
                  FROM SUBTYPE_VALIDATION_MAP WHERE 1=1
                """);
        if (subtypeCode!=null) sb.append(" AND SUBTYPE_CODE=:sc");
        if (binEfectivo!=null) sb.append(" AND BIN_EFECTIVO=:be");
        if (status!=null)      sb.append(" AND STATUS=:st");
        sb.append(" ORDER BY PRIORITY, SUBTYPE_CODE, BIN_EFECTIVO OFFSET :off ROWS FETCH NEXT :sz ROWS ONLY");
        var spec=db.sql(sb.toString());
        if (subtypeCode!=null) spec=spec.bind("sc",subtypeCode);
        if (binEfectivo!=null) spec=spec.bind("be",binEfectivo);
        if (status!=null)      spec=spec.bind("st",status);
        return spec.bind("off",off).bind("sz",s).map(MAP_MAPPER).all();
    }

    @Override
    public Flux<Validation> findResolved(String subtypeCode, String binEfectivo, String status, int page, int size) {
        int p=Math.max(0,page), s=Math.max(1,size), off=p*s;
        var sb=new StringBuilder("""
            SELECT v.VALIDATION_ID, v.CODE, v.DESCRIPTION, v.DATA_TYPE, v.VALUE_FLAG, v.VALUE_NUM, v.VALUE_TEXT,
                   v.STATUS, v.VALID_FROM, v.VALID_TO, v.CREATED_AT, v.UPDATED_AT
              FROM SUBTYPE_VALIDATION_MAP m
              JOIN SUBTYPE_VALIDATION v ON v.VALIDATION_ID = m.VALIDATION_ID
             WHERE m.SUBTYPE_CODE=:sc AND m.BIN_EFECTIVO=:be
            """);
        if (status!=null) sb.append(" AND m.STATUS=:mst");
        sb.append(" ORDER BY m.PRIORITY OFFSET :off ROWS FETCH NEXT :sz ROWS ONLY");

        var spec=db.sql(sb.toString())
                .bind("sc", subtypeCode)
                .bind("be", binEfectivo)
                .bind("off", p*s)
                .bind("sz", s);
        if (status!=null) spec=spec.bind("mst", status);

        return spec.map((r,m) -> Validation.rehydrate(
                r.get("validation_id", Long.class),
                r.get("code", String.class),
                r.get("description", String.class),
                ValidationDataType.valueOf(r.get("data_type", String.class)),
                r.get("value_flag", String.class),
                r.get("value_num", Double.class),
                r.get("value_text", String.class),
                r.get("status", String.class),
                toOffset(r,"valid_from"),
                toOffset(r,"valid_to"),
                toOffset(r,"created_at"),
                toOffset(r,"updated_at")
        )).all();
    }
}
