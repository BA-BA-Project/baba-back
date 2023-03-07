package com.baba.back.baby.domain;

import com.baba.back.relation.domain.Relation;
import com.baba.back.relation.domain.RelationGroup;
import java.util.ArrayList;
import java.util.List;

public class Relations {

    private final List<Relation> values;

    public Relations(List<Relation> values) {
        this.values = new ArrayList<>(values);
    }

    public List<RelationGroup> getMyFamilyGroup() {
        return getRelationGroupBy(true);
    }

    public List<RelationGroup> getOthersFamilyGroup() {
        return getRelationGroupBy(false);
    }

    private List<RelationGroup> getRelationGroupBy(boolean isFamily) {
        return values.stream()
                .map(Relation::getRelationGroup)
                .filter(relation -> relation.isFamily() == isFamily)
                .toList();
    }
}
