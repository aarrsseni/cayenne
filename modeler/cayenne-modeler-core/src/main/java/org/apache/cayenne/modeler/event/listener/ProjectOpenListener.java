package org.apache.cayenne.modeler.event.listener;

import org.apache.cayenne.modeler.event.ProjectOpenEvent;

import java.util.EventListener;

public interface ProjectOpenListener extends EventListener{
    void openProject(ProjectOpenEvent e);
}
