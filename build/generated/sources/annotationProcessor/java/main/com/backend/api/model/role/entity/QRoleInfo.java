package com.backend.api.model.role.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QRoleInfo is a Querydsl query type for RoleInfo
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRoleInfo extends EntityPathBase<RoleInfo> {

    private static final long serialVersionUID = -280084425L;

    public static final QRoleInfo roleInfo = new QRoleInfo("roleInfo");

    public final StringPath aclColumnName = createString("aclColumnName");

    public final DateTimePath<java.time.LocalDateTime> createDate = createDateTime("createDate", java.time.LocalDateTime.class);

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final StringPath prefix = createString("prefix");

    public final NumberPath<Integer> roleId = createNumber("roleId", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> updatedDate = createDateTime("updatedDate", java.time.LocalDateTime.class);

    public final StringPath useYn = createString("useYn");

    public QRoleInfo(String variable) {
        super(RoleInfo.class, forVariable(variable));
    }

    public QRoleInfo(Path<? extends RoleInfo> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRoleInfo(PathMetadata metadata) {
        super(RoleInfo.class, metadata);
    }

}

