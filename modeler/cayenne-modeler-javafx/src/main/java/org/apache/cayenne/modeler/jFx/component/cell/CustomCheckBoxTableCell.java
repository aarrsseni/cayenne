package org.apache.cayenne.modeler.jFx.component.cell;

import javafx.scene.control.cell.CheckBoxTableCell;
import org.apache.cayenne.map.DbRelationship;
import org.apache.cayenne.modeler.BQApplication;
import org.apache.cayenne.modeler.controller.dbEntity.DbRelationshipsController;
import org.apache.cayenne.modeler.observer.Observer;
import org.apache.cayenne.modeler.observer.ObserverDictionary;
import org.apache.cayenne.modeler.jFx.component.Consumer;

public class CustomCheckBoxTableCell extends CheckBoxTableCell{

    private Consumer consumer;

    private DbRelationshipsController dbRelationshipsController;

    public CustomCheckBoxTableCell(Consumer consumer){
        this.consumer = consumer;
        dbRelationshipsController = BQApplication.getInjector().getInstance(DbRelationshipsController.class);
    }

    @Override
    public void updateItem(Object item, boolean empty) {
        if(!empty) {
            Observer observer = (Observer) getTableView().getItems().get(getIndex());
            consumer.consume(this, observer);

            if (item != null && dbRelationshipsController.checkForDepPK((DbRelationship) observer.getBean())) {
                DbRelationship reverseRelationship = ((DbRelationship)observer.getBean()).getReverseRelationship();
                if(reverseRelationship != null){
                    ObserverDictionary.getObserver(reverseRelationship).getPropertyWithoutBinding("toDependentPK").setValue(!(Boolean)item);
                }
            }
        }
        super.updateItem(item, empty);
    }
}
