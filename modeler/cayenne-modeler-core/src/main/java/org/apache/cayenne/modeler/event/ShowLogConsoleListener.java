package org.apache.cayenne.modeler.event;

import java.util.EventListener;

public interface ShowLogConsoleListener extends EventListener{
    void showLogConsole(ShowLogConsoleEvent e);
}
