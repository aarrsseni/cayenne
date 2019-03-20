/*****************************************************************
 *   Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 ****************************************************************/
package org.apache.cayenne.modeler.dialog.autorelationship;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.cayenne.dbsync.naming.ObjectNameGenerator;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.Entity;
import org.apache.cayenne.map.event.MapEvent;
import org.apache.cayenne.map.event.RelationshipEvent;
import org.apache.cayenne.map.relationship.ColumnPair;
import org.apache.cayenne.map.relationship.DbJoin;
import org.apache.cayenne.map.relationship.DbJoinCondition;
import org.apache.cayenne.map.relationship.DbRelationship;
import org.apache.cayenne.map.relationship.SinglePairCondition;
import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.ClassLoadingService;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.dialog.ErrorDebugDialog;
import org.apache.cayenne.modeler.map.relationship.DbJoinModel;
import org.apache.cayenne.modeler.undo.CreateRelationshipUndoableEdit;
import org.apache.cayenne.modeler.undo.InferRelationshipsUndoableEdit;
import org.apache.cayenne.modeler.util.CayenneController;
import org.apache.cayenne.modeler.util.NameGeneratorPreferences;
import org.apache.cayenne.swing.BindingBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InferRelationshipsController extends InferRelationshipsControllerBase {

    public static final int SELECT = 1;
    public static final int CANCEL = 0;

    private static Logger logObj = LoggerFactory.getLogger(ErrorDebugDialog.class);

    protected InferRelationshipsDialog view;

    protected InferRelationshipsTabController entitySelector;

    protected ObjectNameGenerator strategy;

    public InferRelationshipsController(CayenneController parent, DataMap dataMap) {
        super(parent, dataMap);
        strategy = createNamingStrategy(NameGeneratorPreferences
                .getInstance()
                .getLastUsedStrategies()
                .get(0));
        setNamingStrategy(strategy);
        setRelationships();
        this.entitySelector = new InferRelationshipsTabController(this);
    }

    public ObjectNameGenerator createNamingStrategy(String strategyClass) {
        try {
            ClassLoadingService classLoader = application.getClassLoadingService();

            return classLoader.loadClass(ObjectNameGenerator.class, strategyClass).newInstance();
        }
        catch (Throwable th) {
            logObj.error("Error in " + getClass().getName(), th);

            JOptionPane.showMessageDialog(
                    view,
                    "Naming Strategy Initialization Error: " + th.getMessage(),
                    "Naming Strategy Initialization Error",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    @Override
    public Component getView() {
        return view;
    }

    public void startup() {
        // show dialog even on empty DataMap, as custom generation may still take
        // advantage of it

        view = new InferRelationshipsDialog(entitySelector.getView());
        initBindings();

        view.pack();
        view.setModal(true);
        centerView();
        makeCloseableOnEscape();
        view.setVisible(true);

    }

    protected void initBindings() {
        BindingBuilder builder = new BindingBuilder(
                getApplication().getBindingFactory(),
                this);

        builder.bindToAction(view.getCancelButton(), "cancelAction()");
        builder.bindToAction(view.getGenerateButton(), "generateAction()");
        builder.bindToAction(this, "entitySelectedAction()", SELECTED_PROPERTY);
        builder.bindToAction(view.getStrategyCombo(), "strategyComboAction()");
    }

    public void entitySelectedAction() {
        int size = getSelectedEntitiesSize();
        String label;

        if (size == 0) {
            label = "No DbRelationships selected";
        }
        else if (size == 1) {
            label = "One DbRelationships selected";
        }
        else {
            label = size + " DbRelationships selected";
        }

        view.getEntityCount().setText(label);
    }

    public void strategyComboAction() {
        try {

            String strategyClass = (String) view.getStrategyCombo().getSelectedItem();

            this.strategy = createNamingStrategy(strategyClass);

            /**
             * Be user-friendly and update preferences with specified strategy
             */
            if (strategy == null) {
                return;
            }
            NameGeneratorPreferences
                    .getInstance()
                    .addToLastUsedStrategies(strategyClass);
            view.getStrategyCombo().setModel(
                    new DefaultComboBoxModel<>(NameGeneratorPreferences
                            .getInstance()
                            .getLastUsedStrategies()));

        }
        catch (Throwable th) {
            logObj.error("Error in " + getClass().getName(), th);
            return;
        }

        setNamingStrategy(strategy);
        createNames();
        entitySelector.initBindings();
        view.setChoice(SELECT);

    }

    public ObjectNameGenerator getNamingStrategy() {
        return strategy;
    }

    public void cancelAction() {
        view.dispose();
    }

    public void generateAction() {
        
        ProjectController mediator = application
                .getFrameController()
                .getProjectController();
        
        InferRelationshipsUndoableEdit undoableEdit = new InferRelationshipsUndoableEdit();
        for(DbJoinModel dbJoinModel : buildJoins(selectedEntities)) {
            DbJoin dbJoin = dbJoinModel.buildJoin();
            dataMap.addJoin(dbJoin);
            dbJoin.compile(dataMap);
            DbRelationship rel = dbJoin.getRelationhsip();

            RelationshipEvent e = new RelationshipEvent(Application.getFrame(), rel,
                    rel.getSourceEntity(), MapEvent.ADD);
            mediator.fireDbRelationshipEvent(e);

            undoableEdit.addEdit(new CreateRelationshipUndoableEdit(rel.getSourceEntity(), new DbRelationship[] { rel }));
        }
        JOptionPane.showMessageDialog(this.getView(), getSelectedEntitiesSize()
                + " relationships generated");
        view.dispose();
    }

    private List<DbJoinModel> buildJoins(Set<InferredRelationship> inferredRelationships) {
        Set<InferredRelationship> relationships = new HashSet<>(inferredRelationships);
        return inferredRelationships.stream()
                .map(temp -> {
                    if(!relationships.contains(temp)) {
                        return null;
                    }
                    relationships.remove(temp);
                    DbJoinCondition pairCondition = new SinglePairCondition(
                            new ColumnPair(temp.getJoinSource().getName(),
                                    temp.getJoinTarget().getName()));
                    InferredRelationship tempReverseRel = findReverseRelationship(relationships, temp);
                    if(tempReverseRel != null) {
                        relationships.remove(tempReverseRel);
                    }
                    return buildJoinModel(temp, tempReverseRel, pairCondition);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private InferredRelationship findReverseRelationship(Set<InferredRelationship> relationships,
                                                         InferredRelationship temp) {
        for(InferredRelationship targetTemp : relationships) {
            if(isRelationshipReverse(temp, targetTemp)) {
                return targetTemp;
            }
        }
        return null;
    }

    private boolean isRelationshipReverse(InferredRelationship rel1, InferredRelationship rel2) {
        return rel1.getSource().equals(rel2.getTarget()) &&
                rel1.getTarget().equals(rel2.getSource()) &&
                rel1.getJoinSource().equals(rel2.getJoinTarget()) &&
                rel1.getJoinTarget().equals(rel2.getJoinSource());
    }

    private DbJoinModel buildJoinModel(InferredRelationship temp,
                                       InferredRelationship targetTemp,
                                       DbJoinCondition condition) {
        if(temp == null) {
            return null;
        }
        String targetRelName = null;
        boolean targetToMany = false;
        if(targetTemp != null) {
            targetRelName = uniqueRelName(targetTemp.getSource(),
                    targetTemp.getName());
            targetToMany = targetTemp.isToMany();
        }
        DbJoinModel dbJoinModel = new DbJoinModel();
        dbJoinModel.setDbEntities(new DbEntity[]{temp.getSource(), temp.getTarget()});
        dbJoinModel.setNames(new String[]{uniqueRelName(temp.getSource(), temp.getName()), targetRelName});
        dbJoinModel.setToMany(new boolean[]{temp.isToMany(), targetToMany});
        dbJoinModel.setDbJoinCondition(condition);
        dbJoinModel.setDataMap(dataMap);
        return dbJoinModel;
    }

    private String uniqueRelName(Entity entity, String preferredName) {
        int currentSuffix = 1;
        String relName = preferredName;

        while (entity.getRelationship(relName) != null
                || entity.getAttribute(relName) != null) {
            relName = preferredName + currentSuffix;
            currentSuffix++;
        }
        return relName;
    }

}