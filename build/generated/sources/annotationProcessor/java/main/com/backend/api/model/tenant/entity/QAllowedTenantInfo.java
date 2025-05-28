package com.backend.api.model.tenant.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAllowedTenantInfo is a Querydsl query type for AllowedTenantInfo
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAllowedTenantInfo extends EntityPathBase<AllowedTenantInfo> {

    private static final long serialVersionUID = 1980242425L;

    public static final QAllowedTenantInfo allowedTenantInfo = new QAllowedTenantInfo("allowedTenantInfo");

    public final DateTimePath<java.time.LocalDateTime> createdDate = createDateTime("createdDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath tenantId = createString("tenantId");

    public final DateTimePath<java.time.LocalDateTime> updatedDate = createDateTime("updatedDate", java.time.LocalDateTime.class);

    public final NumberPath<Integer> userGroupId = createNumber("userGroupId", Integer.class);

    public QAllowedTenantInfo(String variable) {
        super(AllowedTenantInfo.class, forVariable(variable));
    }

    public QAllowedTenantInfo(Path<? extends AllowedTenantInfo> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAllowedTenantInfo(PathMetadata metadata) {
        super(AllowedTenantInfo.class, metadata);
    }

}

