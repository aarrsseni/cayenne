package org.apache.cayenne.modeler.jFx.component.cell;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class AutoCompleteComboBoxListener<T> implements EventHandler<KeyEvent> {

    private ComboBox<T> comboBox;
    private ObservableList<T> data;
    private boolean moveCaretToPos = false;
    private int caretPos;

    public AutoCompleteComboBoxListener(final ComboBox<T> comboBox) {
        this.comboBox = comboBox;
        data = comboBox.getItems();

        this.comboBox.setEditable(true);
        this.comboBox.setOnKeyPressed(t -> comboBox.hide());
        this.comboBox.setOnKeyReleased(AutoCompleteComboBoxListener.this);
    }

    @Override
    public void handle(KeyEvent event) {
        if(event.getCode() == KeyCode.UP) {
            caretPos = -1;
            moveCaret(comboBox.getEditor().getText().length());
            return;
        } else if(event.getCode() == KeyCode.DOWN) {
            if(!comboBox.isShowing()) {
                comboBox.show();
            }
            caretPos = -1;
            moveCaret(comboBox.getEditor().getText().length());
            return;
        } else if(event.getCode() == KeyCode.BACK_SPACE) {
            moveCaretToPos = true;
            caretPos = comboBox.getEditor().getCaretPosition();
        } else if(event.getCode() == KeyCode.DELETE) {
            moveCaretToPos = true;
            caretPos = comboBox.getEditor().getCaretPosition();
        }

        if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT
                || event.isControlDown() || event.getCode() == KeyCode.HOME
                || event.getCode() == KeyCode.END || event.getCode() == KeyCode.TAB || event.getCode() == KeyCode.ENTER) {
            return;
        }

        ObservableList<T> list = FXCollections.observableArrayList();
        for (T aData : data) {
            if (aData.toString().toLowerCase().startsWith(
                    AutoCompleteComboBoxListener.this.comboBox
                            .getEditor().getText().toLowerCase())) {
                list.add(aData);
            }
        }
        String t = comboBox.getEditor().getText();

        comboBox.hide();
        if(list.size() > 10) {
            comboBox.setVisibleRowCount(10);
        } else {
            comboBox.setVisibleRowCount(list.size());
        }
        comboBox.setItems(list);
        comboBox.show();

        comboBox.getEditor().setText(t);
        if(!moveCaretToPos) {
            caretPos = -1;
        }
        moveCaret(t.length());
        if(!list.isEmpty()) {
            comboBox.show();
        }
    }

    private void moveCaret(int textLength) {
        if(caretPos == -1) {
            comboBox.getEditor().positionCaret(textLength);
        } else {
            comboBox.getEditor().positionCaret(caretPos);
        }
        moveCaretToPos = false;
    }

}