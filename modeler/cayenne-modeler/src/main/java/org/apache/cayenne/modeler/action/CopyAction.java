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
import org.apache.cayenne.configuration.ConfigurationNode;
import org.apache.cayenne.configuration.EmptyConfigurationNodeVisitor;
import org.apache.cayenne.map.*;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.editor.ObjCallbackMethod;
import org.apache.cayenne.modeler.util.CayenneAction;
import org.apache.cayenne.modeler.util.CayenneTransferable;
import org.apache.cayenne.util.XMLEncoder;
import org.apache.cayenne.util.XMLSerializable;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Action for copying entities, queries etc. into system buffer
 */
public class CopyAction extends CayenneAction {

    @Inject
    public PasteAction pasteAction;

    public static String getActionName() {
        return "Copy";
    }

    /**
     * Constructor for CopyAction
     */
    public CopyAction() {
        this(getActionName());
    }

    /**
     * Constructor for descendants
     */
    protected CopyAction(String name) {
        super(name);
    }

    @Override
    public String getIconName() {
        return "icon-copy.png";
    }

    @Override
    public KeyStroke getAcceleratorKey() {
        return KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit
                .getDefaultToolkit()
                .getMenuShortcutKeyMask());
    }

    /**
     * Performs copying of items into system buffer
     */
    @Override
    public void performAction(ActionEvent e) {
        ProjectController mediator = getProjectController();

        Object content = copy(mediator);

        if (content != null) {
            Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
            sysClip.setContents(new CayenneTransferable(content), null);
        }

        // update paste button
       pasteAction.updateState();
    }

    /**
     * Detects selected objects and returns them
     */
    public Object copy(ProjectController mediator) {
        return mediator.getCurrentObject();
    }

    /**
     * Prints an object in XML format to an output stream
     */
    protected void print(XMLEncoder encoder, XMLSerializable object) {
        object.encodeAsXML(encoder, new EmptyConfigurationNodeVisitor());
    }

    /**
     * Returns <code>true</code> if last object in the path contains a removable object.
     */
    @Override
    public boolean enableForPath(ConfigurationNode object) {
        if (object == null) {
            return false;
        }

        if (object instanceof DataMap
                || object instanceof QueryDescriptor
                || object instanceof DbEntity
                || object instanceof ObjEntity
                || object instanceof Embeddable
                || object instanceof EmbeddableAttribute
                || object instanceof DbAttribute
                || object instanceof DbRelationship
                || object instanceof ObjAttribute
                || object instanceof ObjRelationship
                || object instanceof ObjCallbackMethod
                || object instanceof Procedure
                || object instanceof ProcedureParameter) {
            return true;
        }

        return false;
    }
}
