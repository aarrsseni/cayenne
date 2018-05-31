package org.apache.cayenne.modeler.util;

import com.google.inject.Inject;
import javafx.scene.control.TableCell;
import org.apache.cayenne.map.DbRelationship;
import org.apache.cayenne.modeler.controller.DbRelationshipsController;
import org.apache.cayenne.modeler.observer.Observer;
import org.apache.cayenne.modeler.observer.ObserverDictionary;

public class DbRelationshipConsumer implements Consumer{

    @Inject
    public DbRelationshipsController dbRelationshipsController;


    @Override
    public void consume(TableCell tableCell, Observer observer) {
        if ((Boolean) dbRelationshipsController.getDependentPropertyMap().get(observer.getBean()).getValue()) {
            tableCell.setDisable(false);
        } else {
            tableCell.setDisable(true);
        }
    }

    @Override
    public void reverseUpdate(Observer observer, Object item) {
        DbRelationship reverseRelationship = ((DbRelationship)observer.getBean()).getReverseRelationship();
        if(reverseRelationship != null){
            ObserverDictionary.getObserver(reverseRelationship).getPropertyWithoutBinding("toDependentPK").setValue(!(Boolean)item);
        }
    }

}
