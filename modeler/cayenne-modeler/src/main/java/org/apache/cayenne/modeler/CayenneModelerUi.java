/*****************************************************************
 *   Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 ****************************************************************/

package org.apache.cayenne.modeler;

import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import io.bootique.BQCoreModule;
import org.apache.cayenne.di.DIBootstrap;
import org.apache.cayenne.modeler.init.CayenneModelerModule;
import org.apache.cayenne.modeler.pref.CoreDataSourceFactory;
import org.apache.cayenne.modeler.pref.CoreDbAdapterFactory;
import org.apache.cayenne.modeler.pref.DefaultCoreDataSourceFactory;
import org.apache.cayenne.modeler.pref.DefaultCoreDbAdapterFactory;

import java.util.Set;

/**
 * @since 4.1
 */
public class CayenneModelerUi implements com.google.inject.Module{

    private static Multibinder<org.apache.cayenne.di.Module> contributeModuleClass(Binder binder) {
        TypeLiteral<org.apache.cayenne.di.Module> type = new TypeLiteral<org.apache.cayenne.di.Module>() {
        };
        return Multibinder.newSetBinder(binder, type);
    }

    public static void setModuleClass(Binder binder, org.apache.cayenne.di.Module moduleClass) {
        contributeModuleClass(binder).addBinding().to(moduleClass.getClass());
    }

    @Override
    public void configure(Binder binder) {
        BQCoreModule.extend(binder).addCommand(UiCommand.class);
        binder.bind(Launcher.class).to(GenericLauncher.class);
        binder.bind(Application.class).in(Singleton.class);
        binder.bind(CoreDbAdapterFactory.class).to(DefaultCoreDbAdapterFactory.class);
        binder.bind(CoreDataSourceFactory.class).to(DefaultCoreDataSourceFactory.class);

        setModuleClass(binder, new CayenneModelerModule());
    }

    @Provides
    @Inject
    public org.apache.cayenne.di.Injector createInjector(Set<org.apache.cayenne.di.Module> modules) {
        return DIBootstrap.createInjector(modules);
    }

}
