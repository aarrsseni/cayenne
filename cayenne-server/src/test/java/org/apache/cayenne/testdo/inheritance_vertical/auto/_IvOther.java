package org.apache.cayenne.testdo.inheritance_vertical.auto;

import java.util.List;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;
import org.apache.cayenne.testdo.inheritance_vertical.IvImpl;

/**
 * Class _IvOther was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _IvOther extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    public static final String ID_PK_COLUMN = "ID";

    public static final Property<String> NAME = new Property<>("name");
    public static final Property<List<IvImpl>> IMPLS = new Property<>("impls");

    public void setName(String name) {
        writeProperty("name", name);
    }
    public String getName() {
        return (String)readProperty("name");
    }

    public void addToImpls(IvImpl obj) {
        addToManyTarget("impls", obj, true);
    }
    public void removeFromImpls(IvImpl obj) {
        removeToManyTarget("impls", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<IvImpl> getImpls() {
        return (List<IvImpl>)readProperty("impls");
    }


}
