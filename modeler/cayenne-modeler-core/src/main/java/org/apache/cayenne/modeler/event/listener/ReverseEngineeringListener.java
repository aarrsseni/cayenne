package org.apache.cayenne.modeler.event.listener;

import org.apache.cayenne.modeler.event.ReverseEngineeringEvent;

import java.util.EventListener;

public interface ReverseEngineeringListener extends EventListener {
    void reverseEngineeringChanged(ReverseEngineeringEvent e);
}
