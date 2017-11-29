package org.apache.cayenne.testdo.meaningful_pk.auto;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.cayenne.BaseDataObject;
import org.apache.cayenne.exp.Property;

/**
 * Class _MeaningfulPkTest2 was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _MeaningfulPkTest2 extends BaseDataObject {

    private static final long serialVersionUID = 1L; 

    public static final String PK1_PK_COLUMN = "PK1";

    public static final Property<Integer> PK1 = Property.create("pk1", Integer.class);

    protected int pk1;


    public void setPk1(int pk1) {
        beforePropertyWrite("pk1", this.pk1, pk1);
        this.pk1 = pk1;
    }

    public int getPk1() {
        beforePropertyRead("pk1");
        return this.pk1;
    }

    @Override
    public Object readPropertyDirectly(String propName) {
        if(propName == null) {
            throw new IllegalArgumentException();
        }

        switch(propName) {
            case "pk1":
                return this.pk1;
            default:
                return super.readPropertyDirectly(propName);
        }
    }

    @Override
    public void writePropertyDirectly(String propName, Object val) {
        if(propName == null) {
            throw new IllegalArgumentException();
        }

        switch (propName) {
            case "pk1":
                this.pk1 = val == null ? 0 : (int)val;
                break;
            default:
                super.writePropertyDirectly(propName, val);
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        writeSerialized(out);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        readSerialized(in);
    }

    @Override
    protected void writeState(ObjectOutputStream out) throws IOException {
        super.writeState(out);
        out.writeInt(this.pk1);
    }

    @Override
    protected void readState(ObjectInputStream in) throws IOException, ClassNotFoundException {
        super.readState(in);
        this.pk1 = in.readInt();
    }

}
