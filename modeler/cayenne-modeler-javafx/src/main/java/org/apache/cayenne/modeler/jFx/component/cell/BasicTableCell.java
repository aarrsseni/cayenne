package org.apache.cayenne.modeler.jFx.component.cell;

import javafx.event.Event;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import org.apache.cayenne.modeler.observer.Observer;

public class BasicTableCell<O extends Observer, T> extends TableCell<O, T> {

    public BasicTableCell(){
        new CellEvents<>(this).initEventsListeners();
    }

    @Override
    public void commitEdit(T item) {
        if(item != null) {
            TableView<O> table = getTableView();
            if (table != null) {
                TableColumn<O, T> column = getTableColumn();
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
