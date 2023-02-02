package com.baba.back.relation.domain;

public enum RelationGroup {
    FAMILY, MOTHERS, FATHERS, FRIENDS;

    public boolean isFamily() {
        return this == FAMILY;
    }
}
