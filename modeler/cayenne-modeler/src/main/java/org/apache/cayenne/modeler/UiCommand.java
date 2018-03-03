package org.apache.cayenne.modeler;

import com.google.inject.Inject;
import io.bootique.annotation.Args;
import io.bootique.cli.Cli;
import io.bootique.command.Command;
import io.bootique.command.CommandOutcome;
import org.apache.cayenne.di.Module;

import java.util.List;

public class UiCommand implements Command {

    @Inject
    private Launcher abstractLauncher;

    public UiCommand() {
    }



    @Override
    public CommandOutcome run(Cli cli) {
        try {
            abstractLauncher.launch();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return CommandOutcome.succeeded();
    }
}
