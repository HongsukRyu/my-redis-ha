package com.backend.api.model.user.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserExpiredPointInfo is a Querydsl query type for UserExpiredPointInfo
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserExpiredPointInfo extends EntityPathBase<UserExpiredPointInfo> {

    private static final long serialVersionUID = -1251517972L;

    public static final QUserExpiredPointInfo userExpiredPointInfo = new QUserExpiredPointInfo("userExpiredPointInfo");

    public final DateTimePath<java.time.LocalDateTime> expiredDate = createDateTime("expiredDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> expiredId = createNumber("expiredId", Long.class);

    public final NumberPath<Integer> point = createNumber("point", Integer.class);

    public final StringPath userId = createString("userId");

    public QUserExpiredPointInfo(String variable) {
        super(UserExpiredPointInfo.class, forVariable(variable));
    }

    public QUserExpiredPointInfo(Path<? extends UserExpiredPointInfo> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserExpiredPointInfo(PathMetadata metadata) {
        super(UserExpiredPointInfo.class, metadata);
    }

}

