package com.backend.api.model.user.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserGroupInfo is a Querydsl query type for UserGroupInfo
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserGroupInfo extends EntityPathBase<UserGroupInfo> {

    private static final long serialVersionUID = -220046854L;

    public static final QUserGroupInfo userGroupInfo = new QUserGroupInfo("userGroupInfo");

    public final StringPath groupName = createString("groupName");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath isEnable = createString("isEnable");

    public QUserGroupInfo(String variable) {
        super(UserGroupInfo.class, forVariable(variable));
    }

    public QUserGroupInfo(Path<? extends UserGroupInfo> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserGroupInfo(PathMetadata metadata) {
        super(UserGroupInfo.class, metadata);
    }

}

