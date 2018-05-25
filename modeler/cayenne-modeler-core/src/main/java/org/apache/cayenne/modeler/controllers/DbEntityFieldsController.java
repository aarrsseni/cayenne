package org.apache.cayenne.modeler.controllers;

import com.google.inject.Inject;
import org.apache.cayenne.configuration.event.DbEntityEvent;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.util.ExpressionConvertor;
import org.apache.cayenne.util.Util;
import org.apache.cayenne.validation.ValidationException;

public class DbEntityFieldsController {

    @Inject
    public ProjectController projectController;

    public void dbEntityNameChanged(String newName) {
        if (newName != null && newName.trim().length() == 0) {
            newName = null;
        }

        DbEntity entity = projectController.getCurrentState().getDbEntity();

        if (entity == null || Util.nullSafeEquals(newName, entity.getName())) {
            return;
        }

        if (newName == null) {
            throw new ValidationException("Entity name is required.");
        } else if (entity.getDataMap().getDbEntity(newName) == null) {
            // completely new name, set new name for entity
            DbEntityEvent e = new DbEntityEvent(this, entity, entity.getName());
            entity.setName(newName);
            // ProjectUtil.setDbEntityName(entity, newName);
            projectController.fireEvent(e);
        } else {
            // there is an entity with the same name
            throw new ValidationException("There is another entity with name '" + newName + "'.");
        }
    }

    public void dbEntityCatalogChanged(String newCatalog) {
        if (newCatalog != null && newCatalog.trim().length() == 0) {
            newCatalog = null;
        }

        DbEntity ent = projectController.getCurrentState().getDbEntity();

        if (ent != null && !Util.nullSafeEquals(ent.getCatalog(), newCatalog)) {
            ent.setCatalog(newCatalog);
            projectController.fireEvent(new DbEntityEvent(this, ent));
        }
    }

    public void dbEntitySchemaChanged(String newSchema) {
        if (newSchema != null && newSchema.trim().length() == 0) {
            newSchema = null;
        }

        DbEntity ent = projectController.getCurrentState().getDbEntity();

        if (ent != null && !Util.nullSafeEquals(ent.getSchema(), newSchema)) {
            ent.setSchema(newSchema);
            projectController.fireEvent(new DbEntityEvent(this, ent));
        }
    }

    public void dbEntityQualifierChenged(String qualifier) {
        if (qualifier != null && qualifier.trim().length() == 0) {
            qualifier = null;
        }

        DbEntity ent = projectController.getCurrentState().getDbEntity();

        if (ent != null && !Util.nullSafeEquals(ent.getQualifier(), qualifier)) {
            ExpressionConvertor convertor = new ExpressionConvertor();
            try {
                String oldQualifier = convertor.valueAsString(ent.getQualifier());
                if (!Util.nullSafeEquals(oldQualifier, qualifier)) {
                    Expression exp = (Expression) convertor.stringAsValue(qualifier);
                    ent.setQualifier(exp);
                    projectController.fireEvent(new DbEntityEvent(this, ent));
                }
            } catch (IllegalArgumentException ex) {
                // unparsable qualifier
                throw new ValidationException(ex.getMessage());
            }

        }
    }
}
