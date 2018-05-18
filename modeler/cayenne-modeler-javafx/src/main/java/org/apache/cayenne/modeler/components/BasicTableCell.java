package org.apache.cayenne.modeler.components;

import javafx.event.Event;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import org.apache.cayenne.modeler.util.TableColumnEvents;

public class BasicTableCell<Observer, T> extends TableCell<Observer, T> {

    public BasicTableCell(){
        new TableColumnEvents<>(this).initEventsListeners();
    }

    @Override
    public void commitEdit(T item) {
        if(item != null) {
            TableView<Observer> table = getTableView();
            if (table != null) {
                TableColumn<Observer, T> column = getTableColumn();
                TableColumn.CellEditEvent event = new TableColumn.CellEditEvent<>(
                        table, new TablePosition<>(table, getIndex(), column),
                        TableColumn.editCommitEvent(), item
                );
                Event.fireEvent(column, event);
            }
            setItem(item);
            super.commitEdit(getItem());
        }
    }

    public void commit(){}
}
