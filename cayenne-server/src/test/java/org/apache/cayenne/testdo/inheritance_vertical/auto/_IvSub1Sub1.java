package org.apache.cayenne.testdo.inheritance_vertical.auto;

import org.apache.cayenne.exp.Property;
import org.apache.cayenne.testdo.inheritance_vertical.IvSub1;

/**
 * Class _IvSub1Sub1 was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _IvSub1Sub1 extends IvSub1 {

    private static final long serialVersionUID = 1L; 

    public static final String ID_PK_COLUMN = "ID";

    public static final Property<String> SUB1SUB1NAME = new Property<>("sub1Sub1Name");

    public void setSub1Sub1Name(String sub1Sub1Name) {
        writeProperty("sub1Sub1Name", sub1Sub1Name);
    }
    public String getSub1Sub1Name() {
        return (String)readProperty("sub1Sub1Name");
    }

}
