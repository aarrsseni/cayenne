package org.apache.cayenne.modeler.services;

import org.apache.cayenne.modeler.util.BrowserControl;

public class DefaultDocumentationService implements DocumentationService{
    @Override
    public void showDocumentation() {
        BrowserControl.displayURL("http://cayenne.apache.org");
    }
}
