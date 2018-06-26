package org.apache.cayenne.modeler;

import com.google.inject.Injector;
import com.google.inject.Provider;
import io.bootique.cli.Cli;
import io.bootique.command.Command;
import io.bootique.command.CommandOutcome;

public class UiCommand implements Command {

    private Provider<Class<? extends BQApplication>> appProvider;
    private Provider<Injector> injectorProvider;

    private JavaFXLauncher launcher;

    UiCommand(Provider<Class<? extends BQApplication>> appProvider, Provider<Injector> injectorProvider) {
        this.appProvider = appProvider;
        this.injectorProvider = injectorProvider;
        launcher = injectorProvider.get().getInstance(JavaFXLauncher.class);
    }

    @Override
    public CommandOutcome run(Cli cli) {
        BQApplication.INJECTOR = injectorProvider.get();

        launcher.launch(appProvider.get());
        return CommandOutcome.succeeded();
    }
}
