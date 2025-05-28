package com.backend.api.model.aclpolicy.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAclPolicy is a Querydsl query type for AclPolicy
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAclPolicy extends EntityPathBase<AclPolicy> {

    private static final long serialVersionUID = 399650051L;

    public static final QAclPolicy aclPolicy = new QAclPolicy("aclPolicy");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<AclPolicyRole, QAclPolicyRole> roles = this.<AclPolicyRole, QAclPolicyRole>createList("roles", AclPolicyRole.class, QAclPolicyRole.class, PathInits.DIRECT2);

    public final StringPath uri = createString("uri");

    public QAclPolicy(String variable) {
        super(AclPolicy.class, forVariable(variable));
    }

    public QAclPolicy(Path<? extends AclPolicy> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAclPolicy(PathMetadata metadata) {
        super(AclPolicy.class, metadata);
    }

}

