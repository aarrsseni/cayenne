/*****************************************************************
 *   Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 ****************************************************************/
package org.apache.cayenne.modeler.action;

import com.google.inject.Inject;
import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.util.CayenneAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class RedoAction extends CayenneAction {

    @Inject
    public Application application;

    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(b);
    }

    

    @Override
    public boolean isAlwaysOn() {
        return false;
    }

    public static String getActionName() {
        return "Redo";
    }
    
    public RedoAction() {
        super(getActionName());
        setEnabled(true);
    }
    
    @Override
    public KeyStroke getAcceleratorKey() {
        return KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | InputEvent.SHIFT_MASK);
    }
    
    @Override
    public String getIconName() {
        return "icon-redo.png";
    }

    @Override
    public void performAction(ActionEvent e) {
        application.getUndoManager().redo();
    }
}
