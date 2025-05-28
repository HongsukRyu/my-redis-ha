package com.backend.api.model.aclpolicy.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAclPolicyRole is a Querydsl query type for AclPolicyRole
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAclPolicyRole extends EntityPathBase<AclPolicyRole> {

    private static final long serialVersionUID = 1497688089L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAclPolicyRole aclPolicyRole = new QAclPolicyRole("aclPolicyRole");

    public final StringPath allowedMethods = createString("allowedMethods");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QAclPolicy policy;

    public final StringPath roleType = createString("roleType");

    public QAclPolicyRole(String variable) {
        this(AclPolicyRole.class, forVariable(variable), INITS);
    }

    public QAclPolicyRole(Path<? extends AclPolicyRole> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAclPolicyRole(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAclPolicyRole(PathMetadata metadata, PathInits inits) {
        this(AclPolicyRole.class, metadata, inits);
    }

    public QAclPolicyRole(Class<? extends AclPolicyRole> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.policy = inits.isInitialized("policy") ? new QAclPolicy(forProperty("policy")) : null;
    }

}

