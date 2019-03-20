package org.apache.cayenne.project.upgrade;

import java.util.List;

public class RelationshipDescriptor {

    private String name;
    private String reverseName;
    private String srcEntity;
    private String targetEntity;
    private String toMany;
    private String targetToMany;
    private String toDependentPk;
    private String targetToDependentPk;

    private List<AttributePair> attributePairs;

    public RelationshipDescriptor(String name,
                                  String srcEntity,
                                  String targetEntity,
                                  String toMany,
                                  String toDependentPk,
                                  List<AttributePair> attributePairs) {
        this.name = name;
        this.srcEntity = srcEntity;
        this.targetEntity = targetEntity;
        this.toMany = toMany;
        this.toDependentPk = toDependentPk;
        this.attributePairs = attributePairs;
    }

    public boolean isReverse(RelationshipDescriptor currDescriptor) {
        if(srcEntity.equals(currDescriptor.getTargetEntity()) &&
                targetEntity.equals(currDescriptor.getSrcEntity())) {
            for(AttributePair attributePair : attributePairs) {
                boolean wasFound = false;
                for(AttributePair currAttributePair : currDescriptor.getAttributePairs()) {
                    if(attributePair.getLeft().equals(currAttributePair.getRight()) &&
                            attributePair.getRight().equals(currAttributePair.getLeft())) {
                        this.reverseName = currDescriptor.getName();
                        this.targetToMany = currDescriptor.getToMany();
                        this.targetToDependentPk = currDescriptor.getToDependentPk();
                        wasFound = true;
                    }
                }
                if(!wasFound) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public String getSrcEntity() {
        return srcEntity;
    }

    public String getTargetEntity() {
        return targetEntity;
    }

    public String getToMany() {
        return toMany;
    }

    public String getToDependentPk() {
        return toDependentPk;
    }

    public List<AttributePair> getAttributePairs() {
        return attributePairs;
    }

    public String getReverseName() {
        return reverseName;
    }

    public String getTargetToMany() {
        return targetToMany;
    }

    public String getTargetToDependentPk() {
        return targetToDependentPk;
    }
}
