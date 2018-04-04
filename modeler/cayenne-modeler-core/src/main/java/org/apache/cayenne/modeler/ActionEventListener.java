package org.apache.cayenne.modeler;

public class ActionEventListener {

    protected ProjectController projectController;

    public ActionEventListener(ProjectController projectController){
        this.projectController = projectController;
        initListeners();
    }

    public void initListeners() {

    }
}
