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
package org.apache.cayenne.modeler.action.listener;

import org.apache.cayenne.dbsync.filter.NamePatternMatcher;
import org.apache.cayenne.dbsync.merge.context.EntityMergeSupport;
import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.dialog.objentity.EntitySyncDialog;
import org.apache.cayenne.modeler.event.ConfirmMeaningfulFKsEvent;
import org.apache.cayenne.modeler.event.listener.ConfirmMeaningfulFKsListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @since 4.1
 */
public class ConfirmMeaningfulFKsActionListener implements ConfirmMeaningfulFKsListener{

    private EntitySyncDialog view;

    @Override
    public void confirmMeaningfulFKs(ConfirmMeaningfulFKsEvent e) {
        final boolean[] cancel = {false};
        final boolean[] removeFKs = {true};

        view = new EntitySyncDialog();

        view.getUpdateButton().addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                removeFKs[0] = view.getRemoveFKs().isSelected();
                view.dispose();
            }
        });

        view.getCancelButton().addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                cancel[0] = true;
                view.dispose();
            }
        });

        view.pack();
        view.setModal(true);
        Application.getInstance().getFrameController().centerView();
        Application.getInstance().getFrameController().makeCloseableOnEscape();
        view.setVisible(true);

        // TODO: Modeler-controlled defaults for all the hardcoded flags here.
        if(!cancel[0]) {
            e.setMerger(new EntityMergeSupport(e.getNamingStrategy(), NamePatternMatcher.EXCLUDE_ALL, removeFKs[0], true, false));
        }
    }
}
