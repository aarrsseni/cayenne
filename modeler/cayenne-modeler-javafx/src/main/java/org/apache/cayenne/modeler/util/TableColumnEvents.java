package org.apache.cayenne.modeler.util;

import javafx.scene.control.TablePosition;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.apache.cayenne.modeler.components.BasicTableCell;

public class TableColumnEvents<S, T> {

    private BasicTableCell<S, T> customCell;

    public TableColumnEvents(BasicTableCell<S, T> customCell) {
        this.customCell = customCell;
    }

    public void initEventsListeners(){
        customCell.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            TablePosition focusedCellPosition = customCell.getTableView().getFocusModel().getFocusedCell();
            customCell.getTableView().getFocusModel().focus(focusedCellPosition);
            customCell.getTableView().edit(focusedCellPosition.getRow(), customCell.getTableColumn());
        });

        customCell.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if(event.getCode() == KeyCode.ENTER) {
                if(customCell.isEditing()) {
                    customCell.commit();
                } else {
                    TablePosition focusedCellPosition = customCell.getTableView().getFocusModel().getFocusedCell();
                    customCell.getTableView().getFocusModel().focus(focusedCellPosition);
                    customCell.getTableView().edit(focusedCellPosition.getRow(), customCell.getTableColumn());
                }
            }
        });
    }
}
