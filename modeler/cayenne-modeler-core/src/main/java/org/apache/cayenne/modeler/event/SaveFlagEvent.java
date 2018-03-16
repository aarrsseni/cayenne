package org.apache.cayenne.modeler.event;

import java.util.EventObject;

/*
 * @since 4.1
 */
public class SaveFlagEvent extends EventObject {

    protected boolean dirty;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public SaveFlagEvent(Object source) {
        super(source);
    }

    public SaveFlagEvent(Object source, boolean dirty){
        super(source);
        this.dirty = dirty;
    }

    public boolean getDirty(){
        return dirty;
    }
}
