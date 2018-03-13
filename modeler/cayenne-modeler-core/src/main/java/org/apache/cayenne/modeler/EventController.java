package org.apache.cayenne.modeler;

public class EventController {

    protected EventListenerMap listenerMap;

    public EventController(){
        this.listenerMap = new EventListenerMap();
    }

    public void reset(){
        this.listenerMap = new EventListenerMap();
    }
}
