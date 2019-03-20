package org.apache.cayenne.map.relationship;

import java.io.Serializable;

public class ColumnPair implements Serializable {

    private String left;
    private String right;

    public ColumnPair(){}

    public ColumnPair(String left, String right) {
        this.left = left;
        this.right = right;
    }

    public String getLeft() {
        return left;
    }

    public String getRight() {
        return right;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public void setRight(String right) {
        this.right = right;
    }

}
