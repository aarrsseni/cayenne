package org.apache.cayenne.modeler.jFx.component;

import javafx.scene.control.TableCell;
import org.apache.cayenne.modeler.observer.Observer;

public interface Consumer {
    void consume(TableCell tableCell, Observer observer);
}
