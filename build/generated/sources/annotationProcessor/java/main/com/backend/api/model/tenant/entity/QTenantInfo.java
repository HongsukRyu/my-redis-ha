package com.backend.api.model.tenant.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTenantInfo is a Querydsl query type for TenantInfo
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTenantInfo extends EntityPathBase<TenantInfo> {

    private static final long serialVersionUID = 2144082143L;

    public static final QTenantInfo tenantInfo = new QTenantInfo("tenantInfo");

    public final DateTimePath<java.time.LocalDateTime> createDate = createDateTime("createDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath tenantDesc = createString("tenantDesc");

    public final StringPath tenantId = createString("tenantId");

    public final StringPath tenantName = createString("tenantName");

    public final DateTimePath<java.time.LocalDateTime> updatedDate = createDateTime("updatedDate", java.time.LocalDateTime.class);

    public final StringPath useYn = createString("useYn");

    public QTenantInfo(String variable) {
        super(TenantInfo.class, forVariable(variable));
    }

    public QTenantInfo(Path<? extends TenantInfo> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTenantInfo(PathMetadata metadata) {
        super(TenantInfo.class, metadata);
    }

}

