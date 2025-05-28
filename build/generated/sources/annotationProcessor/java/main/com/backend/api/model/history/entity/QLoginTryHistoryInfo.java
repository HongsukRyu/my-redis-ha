package com.backend.api.model.history.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QLoginTryHistoryInfo is a Querydsl query type for LoginTryHistoryInfo
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLoginTryHistoryInfo extends EntityPathBase<LoginTryHistoryInfo> {

    private static final long serialVersionUID = -1308909825L;

    public static final QLoginTryHistoryInfo loginTryHistoryInfo = new QLoginTryHistoryInfo("loginTryHistoryInfo");

    public final StringPath clientIp = createString("clientIp");

    public final DateTimePath<java.time.LocalDateTime> createDate = createDateTime("createDate", java.time.LocalDateTime.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath userEmail = createString("userEmail");

    public final StringPath userId = createString("userId");

    public final NumberPath<Long> userType = createNumber("userType", Long.class);

    public QLoginTryHistoryInfo(String variable) {
        super(LoginTryHistoryInfo.class, forVariable(variable));
    }

    public QLoginTryHistoryInfo(Path<? extends LoginTryHistoryInfo> path) {
        super(path.getType(), path.getMetadata());
    }

    public QLoginTryHistoryInfo(PathMetadata metadata) {
        super(LoginTryHistoryInfo.class, metadata);
    }

}

