package com.baba.back.relation.domain;

public enum RelationGroup {
    FAMILY("FAMILY"), MOTHERS("MOTHERS"), FATHERS("FATHERS"), FRIENDS("FRIENDS");

    private final String relationGroup;

    RelationGroup(String relationGroup) {
        this.relationGroup = relationGroup;
    }

    public boolean isFamily() {
        return relationGroup.equals(FAMILY.name());
    }
}
