package org.apache.cayenne.modeler;

import org.apache.cayenne.modeler.action.listener.*;
import org.apache.cayenne.modeler.event.*;

public class ModelerActionListener {

    protected CayenneModelerController cayenneModelerController;

    public ModelerActionListener(CayenneModelerController cayenneModelerController) {
        this.cayenneModelerController = cayenneModelerController;
        initListeners();
    }

    public void initListeners() {
        cayenneModelerController.getProjectController().getEventController().addListener(ProjectOpenListener.class, new ProjectOpenActionListener(cayenneModelerController));
        cayenneModelerController.getProjectController().getEventController().addListener(CreateAttributeListener.class, new CreateAttributeActionListener());
        cayenneModelerController.getProjectController().getEventController().addListener(CreateDataMapListener.class, new CreateDataMapActionListener());
        cayenneModelerController.getProjectController().getEventController().addListener(CreateDbEntityListener.class, new CreateDbEntityActionListener());
        cayenneModelerController.getProjectController().getEventController().addListener(CreateEmbeddableListener.class, new CreateEmbeddableActionListener());
        cayenneModelerController.getProjectController().getEventController().addListener(CreateNodeListener.class, new CreateNodeActionListener());
        cayenneModelerController.getProjectController().getEventController().addListener(CreateProcedureListener.class, new CreateProcedureActionListener());
        cayenneModelerController.getProjectController().getEventController().addListener(CreateProcedureParameterListener.class, new CreateProcedureParameterActionListener());
        cayenneModelerController.getProjectController().getEventController().addListener(CreateRelationshipListener.class, new CreateRelationshipActionListener());
        cayenneModelerController.getProjectController().getEventController().addListener(LinkDataMapsListener.class, new LinkDataMapsActionListener());
        cayenneModelerController.getProjectController().getEventController().addListener(CreateObjEntityListener.class, new CreateObjEntityActionListener());
        cayenneModelerController.getProjectController().getEventController().addListener(CreateCallbackMethodListener.class, new CreateCallbackMethodActionListener());
        cayenneModelerController.getProjectController().getEventController().addListener(GenerateDbListener.class, new GenerateDbActionListener());
        cayenneModelerController.getProjectController().getEventController().addListener(LinkDataMapListener.class, new LinkDataMapActionListener());
        cayenneModelerController.getProjectController().getEventController().addListener(GenerateCodeListener.class, new GenerateCodeActionListener());
    }
}
