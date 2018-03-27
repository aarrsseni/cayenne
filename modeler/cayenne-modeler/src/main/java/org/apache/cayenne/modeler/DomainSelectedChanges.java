package org.apache.cayenne.modeler;

import org.apache.cayenne.modeler.action.ActionManager;
import org.apache.cayenne.modeler.event.DomainDisplayEvent;
import org.apache.cayenne.modeler.event.DomainDisplayListener;

/**
 * @since 4.1
 * Class is used to call different methods depending on whether domain was opened or closed
 */
public class DomainSelectedChanges implements DomainDisplayListener{

    protected ActionManager actionManager;
    protected CayenneModelerController modelerController;

    public DomainSelectedChanges(ActionManager actionManager, CayenneModelerController modelerController) {
        this.actionManager = actionManager;
        this.modelerController = modelerController;
    }

    @Override
    public void currentDomainChanged(DomainDisplayEvent e) {
        if(e.getDomain() == null){
            actionManager.projectOpened();
        } else {
            actionManager.domainSelected();
        }
    }

    public void initAll() {
        modelerController.getProjectController().getEventController().addDomainDisplayListener(this);
    }
}
