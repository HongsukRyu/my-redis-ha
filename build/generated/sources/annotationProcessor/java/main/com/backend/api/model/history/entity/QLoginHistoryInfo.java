package com.backend.api.model.history.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QLoginHistoryInfo is a Querydsl query type for LoginHistoryInfo
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLoginHistoryInfo extends EntityPathBase<LoginHistoryInfo> {

    private static final long serialVersionUID = -1626031606L;

    public static final QLoginHistoryInfo loginHistoryInfo = new QLoginHistoryInfo("loginHistoryInfo");

    public final StringPath clientIp = createString("clientIp");

    public final DateTimePath<java.time.LocalDateTime> createDate = createDateTime("createDate", java.time.LocalDateTime.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath userEmail = createString("userEmail");

    public final StringPath userId = createString("userId");

    public final NumberPath<Long> userType = createNumber("userType", Long.class);

    public QLoginHistoryInfo(String variable) {
        super(LoginHistoryInfo.class, forVariable(variable));
    }

    public QLoginHistoryInfo(Path<? extends LoginHistoryInfo> path) {
        super(path.getType(), path.getMetadata());
    }

    public QLoginHistoryInfo(PathMetadata metadata) {
        super(LoginHistoryInfo.class, metadata);
    }

}

