package com.ooad.xproject.constant;

public enum RoleType {
    Teacher("T"),
    Student("S"),
    Admin("A"),
    Null("N");

    public String code;

    RoleType(String code) {
        this.code = code;
    }

    public static RoleType getRoleType(String code) {
        for (RoleType i : RoleType.values()) {
            if (i.match(code)) {
                return i;
            }
        }
        return Null;
    }

    /**
     * Return ture if the enum match the type code
     */
    public boolean match(String code) {
        return this.code.equals(code);
    }
}
