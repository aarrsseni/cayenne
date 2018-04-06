package org.apache.cayenne.modeler.event.listener;

import org.apache.cayenne.modeler.event.ShowLogConsoleEvent;

import java.util.EventListener;

public interface ShowLogConsoleListener extends EventListener{
    void showLogConsole(ShowLogConsoleEvent e);
}
