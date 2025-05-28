package com.backend.api.model.user.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserPointCategory is a Querydsl query type for UserPointCategory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserPointCategory extends EntityPathBase<UserPointCategory> {

    private static final long serialVersionUID = 261745243L;

    public static final QUserPointCategory userPointCategory = new QUserPointCategory("userPointCategory");

    public final NumberPath<Long> categoryId = createNumber("categoryId", Long.class);

    public final StringPath categoryName = createString("categoryName");

    public final DateTimePath<java.time.LocalDateTime> createDate = createDateTime("createDate", java.time.LocalDateTime.class);

    public final StringPath description = createString("description");

    public final DateTimePath<java.time.LocalDateTime> updatedDate = createDateTime("updatedDate", java.time.LocalDateTime.class);

    public QUserPointCategory(String variable) {
        super(UserPointCategory.class, forVariable(variable));
    }

    public QUserPointCategory(Path<? extends UserPointCategory> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserPointCategory(PathMetadata metadata) {
        super(UserPointCategory.class, metadata);
    }

}

