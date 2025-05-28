package com.backend.api.model.user.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserPointInfo is a Querydsl query type for UserPointInfo
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserPointInfo extends EntityPathBase<UserPointInfo> {

    private static final long serialVersionUID = -1467496309L;

    public static final QUserPointInfo userPointInfo = new QUserPointInfo("userPointInfo");

    public final DateTimePath<java.time.LocalDateTime> lastUpdatedDate = createDateTime("lastUpdatedDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> totalPoint = createNumber("totalPoint", Long.class);

    public final StringPath userId = createString("userId");

    public QUserPointInfo(String variable) {
        super(UserPointInfo.class, forVariable(variable));
    }

    public QUserPointInfo(Path<? extends UserPointInfo> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserPointInfo(PathMetadata metadata) {
        super(UserPointInfo.class, metadata);
    }

}

