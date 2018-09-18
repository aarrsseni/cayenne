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
import org.apache.cayenne.modeler.editor.ObjEntityAttributePanel;
import org.apache.cayenne.modeler.editor.dbentity.DbEntityAttributePanel;

import javax.swing.JComponent;
import java.awt.event.ActionEvent;


public class CutAttributeRelationshipAction extends CutAction implements MultipleObjectsAction {

    @Inject
    private CutAttributeAction cutAttributeAction;

    @Inject
    private CutRelationshipAction cutRelationshipAction;

    private JComponent currentSelectedPanel;

    public CutAttributeRelationshipAction() {
        super();
    }

    public JComponent getCurrentSelectedPanel() {
        return currentSelectedPanel;
    }

    public void setCurrentSelectedPanel(JComponent currentSelectedPanel) {
        this.currentSelectedPanel = currentSelectedPanel;
    }

    public String getActionName(boolean multiple) {
        if (currentSelectedPanel instanceof ObjEntityAttributePanel || currentSelectedPanel instanceof DbEntityAttributePanel) {
            return cutAttributeAction.getActionName(multiple);
        } else {
            return cutRelationshipAction.getActionName(multiple);
        }
    }

    public boolean enableForPath(ConfigurationNode object) {
        if (currentSelectedPanel instanceof ObjEntityAttributePanel || currentSelectedPanel instanceof DbEntityAttributePanel) {
            return cutAttributeAction.enableForPath(object);
        } else {
            return cutRelationshipAction.enableForPath(object);
        }
    }

    public void performAction(ActionEvent e) {
        if (currentSelectedPanel instanceof ObjEntityAttributePanel || currentSelectedPanel instanceof DbEntityAttributePanel) {
            cutAttributeAction.performAction(e);
        } else {
            cutRelationshipAction.performAction(e);
        }
    }

}