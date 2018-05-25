package org.apache.cayenne.modeler.controllers;

import com.google.inject.Inject;
import org.apache.cayenne.configuration.event.ObjEntityEvent;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.util.Util;
import org.apache.cayenne.validation.ValidationException;

public class ObjEntityFieldsController {

    @Inject
    public ProjectController projectController;

    public void objEntityNameChanged(String newName){
        if (newName != null && newName.trim().length() == 0) {
            newName = null;
        }

        ObjEntity entity = projectController.getCurrentState().getObjEntity();
        if (entity == null) {
            return;
        }

        if (Util.nullSafeEquals(newName, entity.getName())) {
            return;
        }

        if (newName == null) {
            throw new ValidationException("Entity name is required.");
        } else if (entity.getDataMap().getObjEntity(newName) == null) {
            // completely new name, set new name for entity
            ObjEntityEvent e = new ObjEntityEvent(this, entity, entity.getName());
            entity.setName(newName);

            projectController.fireEvent(e);

            //TODO Allow user to choose changing class name !!!
            entity.setClassName(newName);
            //TODO Use to update class name when objEntity name was changed.
            // suggest to update class name
//            ClassNameUpdater nameUpdater = new ClassNameUpdater(Application.getInstance().getFrameController(), entity);
//
//            if (nameUpdater.doNameUpdate()) {
//                className.setText(entity.getClassName());
//                clientClassName.setText(entity.getClientClassName());
//            }
        } else {
            // there is an entity with the same name
            throw new ValidationException("There is another entity with name '" + newName + "'.");
        }

    }

    public void objEntitySuperclassChanged(String newValue) {
        if (newValue != null && newValue.trim().length() == 0) {
            newValue = null;
        }

        ObjEntity ent = projectController.getCurrentState().getObjEntity();

        if (ent != null && !Util.nullSafeEquals(ent.getClientSuperClassName(), newValue)) {
            ent.setClientSuperClassName(newValue);
            projectController.fireEvent(new ObjEntityEvent(this, ent));
        }
    }

    public void objEntityClassNameChanged(String className) {
        if (className != null && className.trim().length() == 0) {
            className = null;
        }

        ObjEntity entity = projectController.getCurrentState().getObjEntity();

        // "ent" may be null if we quit editing by changing tree selection
        if (entity != null && !Util.nullSafeEquals(entity.getClassName(), className)) {
            entity.setClassName(className);
            projectController.fireEvent(new ObjEntityEvent(this, entity));
        }
    }

    public void isAbstractChanged(boolean isSelected) {
        ObjEntity entity = projectController.getCurrentState().getObjEntity();
        if (entity != null) {
            entity.setAbstract(isSelected);
            projectController.fireEvent(new ObjEntityEvent(this, entity));
        }
    }

    public void isReadOnlyChanged(boolean isReadOnly) {
        ObjEntity entity = projectController.getCurrentState().getObjEntity();
        if (entity != null) {
            entity.setReadOnly(isReadOnly);
            projectController.fireEvent(new ObjEntityEvent(this, entity));
        }
    }

    public void dbEntityChanged(DbEntity dbEntity) {
        ObjEntity entity = projectController.getCurrentState().getObjEntity();

        if (dbEntity != entity.getDbEntity()) {
            entity.setDbEntity(dbEntity);
            projectController.fireEvent(new ObjEntityEvent(this, entity));
        }
    }
}
