package com.backend.api.common.object;

public enum Role {
    USER("USER", "st", 1),
    ADMIN("ADMIN", "sm", 3);
    private final String userRole;
    private final int roleType;

    Role(String role, String prefix, int roleType) {
        this.userRole = role;
        this.roleType = roleType;
    }

    public String role() {
        return userRole;
    }

    public Long roleType() {
        return (long) roleType;
    }

    public static boolean isUser(int type) {
        return type == USER.roleType;
    }

    public static boolean isAdmin(int type) {
        return type == ADMIN.roleType;
    }

}
