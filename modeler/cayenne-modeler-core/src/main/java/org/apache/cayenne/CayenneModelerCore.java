package org.apache.cayenne;

import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Module;
import org.apache.cayenne.service.Service;
import org.apache.cayenne.service.ServiceImpl;

import java.util.Objects;

public class CayenneModelerCore implements Module{

    @Override
    public void configure(Binder binder) {
        binder.bind(Service.class).to(ServiceImpl.class);
    }
}
