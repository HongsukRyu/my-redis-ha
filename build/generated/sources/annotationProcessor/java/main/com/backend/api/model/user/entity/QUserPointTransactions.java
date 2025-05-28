package com.backend.api.model.user.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserPointTransactions is a Querydsl query type for UserPointTransactions
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserPointTransactions extends EntityPathBase<UserPointTransactions> {

    private static final long serialVersionUID = -590299086L;

    public static final QUserPointTransactions userPointTransactions = new QUserPointTransactions("userPointTransactions");

    public final StringPath assignedBy = createString("assignedBy");

    public final DateTimePath<java.time.LocalDateTime> createDate = createDateTime("createDate", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> expireDate = createDateTime("expireDate", java.time.LocalDateTime.class);

    public final NumberPath<Integer> point = createNumber("point", Integer.class);

    public final NumberPath<Long> pointCategoryId = createNumber("pointCategoryId", Long.class);

    public final NumberPath<Long> trnId = createNumber("trnId", Long.class);

    public final EnumPath<PointType> type = createEnum("type", PointType.class);

    public final StringPath userId = createString("userId");

    public QUserPointTransactions(String variable) {
        super(UserPointTransactions.class, forVariable(variable));
    }

    public QUserPointTransactions(Path<? extends UserPointTransactions> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserPointTransactions(PathMetadata metadata) {
        super(UserPointTransactions.class, metadata);
    }

}

