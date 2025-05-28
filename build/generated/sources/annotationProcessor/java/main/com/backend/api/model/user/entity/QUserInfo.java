package com.backend.api.model.user.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserInfo is a Querydsl query type for UserInfo
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserInfo extends EntityPathBase<UserInfo> {

    private static final long serialVersionUID = -1786635679L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserInfo userInfo = new QUserInfo("userInfo");

    public final DateTimePath<java.time.Instant> createDate = createDateTime("createDate", java.time.Instant.class);

    public final StringPath email = createString("email");

    public final StringPath encodedPassword = createString("encodedPassword");

    public final DateTimePath<java.time.Instant> expireDate = createDateTime("expireDate", java.time.Instant.class);

    public final StringPath expireYn = createString("expireYn");

    public final StringPath name = createString("name");

    public final StringPath phone = createString("phone");

    public final NumberPath<Integer> status = createNumber("status", Integer.class);

    public final com.backend.api.model.role.entity.QRoleInfo type;

    public final QUserGroupInfo userGroup;

    public final StringPath userId = createString("userId");

    public QUserInfo(String variable) {
        this(UserInfo.class, forVariable(variable), INITS);
    }

    public QUserInfo(Path<? extends UserInfo> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserInfo(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserInfo(PathMetadata metadata, PathInits inits) {
        this(UserInfo.class, metadata, inits);
    }

    public QUserInfo(Class<? extends UserInfo> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.type = inits.isInitialized("type") ? new com.backend.api.model.role.entity.QRoleInfo(forProperty("type")) : null;
        this.userGroup = inits.isInitialized("userGroup") ? new QUserGroupInfo(forProperty("userGroup")) : null;
    }

}

