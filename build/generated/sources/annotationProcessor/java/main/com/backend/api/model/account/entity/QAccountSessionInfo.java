package com.backend.api.model.account.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAccountSessionInfo is a Querydsl query type for AccountSessionInfo
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAccountSessionInfo extends EntityPathBase<AccountSessionInfo> {

    private static final long serialVersionUID = 1360953825L;

    public static final QAccountSessionInfo accountSessionInfo = new QAccountSessionInfo("accountSessionInfo");

    public final StringPath clientIp = createString("clientIp");

    public final NumberPath<Integer> expirationTime = createNumber("expirationTime", Integer.class);

    public final DateTimePath<java.util.Date> lastLoginDate = createDateTime("lastLoginDate", java.util.Date.class);

    public final DateTimePath<java.util.Date> lastRefreshTime = createDateTime("lastRefreshTime", java.util.Date.class);

    public final StringPath refreshToken = createString("refreshToken");

    public final StringPath token = createString("token");

    public final StringPath userId = createString("userId");

    public QAccountSessionInfo(String variable) {
        super(AccountSessionInfo.class, forVariable(variable));
    }

    public QAccountSessionInfo(Path<? extends AccountSessionInfo> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAccountSessionInfo(PathMetadata metadata) {
        super(AccountSessionInfo.class, metadata);
    }

}

