package org.apache.cayenne.modeler.services;

import org.apache.cayenne.map.Embeddable;
import org.apache.cayenne.map.EmbeddableAttribute;
import org.apache.cayenne.map.EmbeddedAttribute;
import org.apache.cayenne.map.ObjAttribute;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.map.ObjRelationship;
import org.apache.cayenne.modeler.util.CodeValidationUtil;
import org.apache.cayenne.validation.BeanValidationFailure;
import org.apache.cayenne.validation.SimpleValidationFailure;
import org.apache.cayenne.validation.ValidationFailure;
import org.apache.cayenne.validation.ValidationResult;

public class DefaultCgenValidationService implements CgenValidationService {

    @Override
    public void validateEmbeddable(ValidationResult validationBuffer, Embeddable embeddable) {
        ValidationFailure embeddableFailure = validateEmbeddable(embeddable);
        if (embeddableFailure != null) {
            validationBuffer.addFailure(embeddableFailure);
            return;
        }

        for (EmbeddableAttribute attribute : embeddable.getAttributes()) {
            ValidationFailure failure = validateEmbeddableAttribute(attribute);
            if (failure != null) {
                validationBuffer.addFailure(failure);
                return;
            }
        }
    }

    private ValidationFailure validateEmbeddableAttribute(EmbeddableAttribute attribute) {
        String name = attribute.getEmbeddable().getClassName();

        ValidationFailure emptyName = BeanValidationFailure.validateNotEmpty(name, "attribute.name",
                attribute.getName());
        if (emptyName != null) {
            return emptyName;
        }

        ValidationFailure badName = CodeValidationUtil.validateJavaIdentifier(name, "attribute.name",
                attribute.getName());
        if (badName != null) {
            return badName;
        }

        ValidationFailure emptyType = BeanValidationFailure.validateNotEmpty(name, "attribute.type",
                attribute.getType());
        if (emptyType != null) {
            return emptyType;
        }

        ValidationFailure badType = BeanValidationFailure.validateJavaClassName(name, "attribute.type",
                attribute.getType());
        if (badType != null) {
            return badType;
        }

        return null;
    }

    private ValidationFailure validateEmbeddable(Embeddable embeddable) {

        String name = embeddable.getClassName();

        ValidationFailure emptyClass = BeanValidationFailure.validateNotEmpty(name, "className",
                embeddable.getClassName());
        if (emptyClass != null) {
            return emptyClass;
        }

        ValidationFailure badClass = BeanValidationFailure.validateJavaClassName(name, "className",
                embeddable.getClassName());
        if (badClass != null) {
            return badClass;
        }

        return null;
    }

    @Override
    public void validateEntity(ValidationResult validationBuffer, ObjEntity entity, boolean clientValidation) {
        ValidationFailure entityFailure = validateEntity(clientValidation ? entity.getClientEntity() : entity);
        if (entityFailure != null) {
            validationBuffer.addFailure(entityFailure);
            return;
        }

        for (ObjAttribute attribute : entity.getAttributes()) {
            if (attribute instanceof EmbeddedAttribute) {
                EmbeddedAttribute embeddedAttribute = (EmbeddedAttribute) attribute;
                for (ObjAttribute subAttribute : embeddedAttribute.getAttributes()) {
                    ValidationFailure failure = validateEmbeddedAttribute(subAttribute);
                    if (failure != null) {
                        validationBuffer.addFailure(failure);
                        return;
                    }
                }
            } else {

                ValidationFailure failure = validateAttribute(attribute);
                if (failure != null) {
                    validationBuffer.addFailure(failure);
                    return;
                }
            }
        }

        for (ObjRelationship rel : entity.getRelationships()) {
            ValidationFailure failure = validateRelationship(rel, clientValidation);
            if (failure != null) {
                validationBuffer.addFailure(failure);
                return;
            }
        }
    }

    private ValidationFailure validateEntity(ObjEntity entity) {

        String name = entity.getName();

        if (entity.isGeneric()) {
            return new SimpleValidationFailure(name, "Generic class");
        }

        ValidationFailure emptyClass = BeanValidationFailure.validateNotEmpty(name, "className", entity.getClassName());
        if (emptyClass != null) {
            return emptyClass;
        }

        ValidationFailure badClass = BeanValidationFailure.validateJavaClassName(name, "className",
                entity.getClassName());
        if (badClass != null) {
            return badClass;
        }

        if (entity.getSuperClassName() != null) {
            ValidationFailure badSuperClass = BeanValidationFailure.validateJavaClassName(name, "superClassName",
                    entity.getSuperClassName());
            if (badSuperClass != null) {
                return badSuperClass;
            }
        }

        return null;
    }

    private ValidationFailure validateAttribute(ObjAttribute attribute) {

        String name = attribute.getEntity().getName();

        ValidationFailure emptyName = BeanValidationFailure.validateNotEmpty(name, "attribute.name",
                attribute.getName());
        if (emptyName != null) {
            return emptyName;
        }

        ValidationFailure badName = CodeValidationUtil.validateJavaIdentifier(name, "attribute.name",
                attribute.getName());
        if (badName != null) {
            return badName;
        }

        ValidationFailure emptyType = BeanValidationFailure.validateNotEmpty(name, "attribute.type",
                attribute.getType());
        if (emptyType != null) {
            return emptyType;
        }

        ValidationFailure badType = BeanValidationFailure.validateJavaClassName(name, "attribute.type",
                attribute.getType());
        if (badType != null) {
            return badType;
        }

        return null;
    }

    private ValidationFailure validateEmbeddedAttribute(ObjAttribute attribute) {

        String name = attribute.getEntity().getName();

        // validate embeddedAttribute and attribute names
        // embeddedAttribute returned attibute as
        // [name_embeddedAttribute].[name_attribute]
        String[] attributes = attribute.getName().split("\\.");
        String nameEmbeddedAttribute = attributes[0];
        int beginIndex = attributes[0].length();
        String attr = attribute.getName().substring(beginIndex + 1);

        ValidationFailure emptyEmbeddedName = BeanValidationFailure.validateNotEmpty(name, "attribute.name",
                nameEmbeddedAttribute);
        if (emptyEmbeddedName != null) {
            return emptyEmbeddedName;
        }

        ValidationFailure badEmbeddedName = CodeValidationUtil.validateJavaIdentifier(name, "attribute.name",
                nameEmbeddedAttribute);
        if (badEmbeddedName != null) {
            return badEmbeddedName;
        }

        ValidationFailure emptyName = BeanValidationFailure.validateNotEmpty(name, "attribute.name", attr);
        if (emptyName != null) {
            return emptyName;
        }

        ValidationFailure badName = CodeValidationUtil.validateJavaIdentifier(name, "attribute.name", attr);
        if (badName != null) {
            return badName;
        }

        ValidationFailure emptyType = BeanValidationFailure.validateNotEmpty(name, "attribute.type",
                attribute.getType());
        if (emptyType != null) {
            return emptyType;
        }

        ValidationFailure badType = BeanValidationFailure.validateJavaClassName(name, "attribute.type",
                attribute.getType());
        if (badType != null) {
            return badType;
        }

        return null;
    }

    private ValidationFailure validateRelationship(ObjRelationship relationship, boolean clientValidation) {

        String name = relationship.getSourceEntity().getName();

        ValidationFailure emptyName = BeanValidationFailure.validateNotEmpty(name, "relationship.name",
                relationship.getName());
        if (emptyName != null) {
            return emptyName;
        }

        ValidationFailure badName = CodeValidationUtil.validateJavaIdentifier(name, "relationship.name",
                relationship.getName());
        if (badName != null) {
            return badName;
        }

        if (!relationship.isToMany()) {

            ObjEntity targetEntity = relationship.getTargetEntity();

            if (clientValidation && targetEntity != null) {
                targetEntity = targetEntity.getClientEntity();
            }

            if (targetEntity == null) {

                return new BeanValidationFailure(name, "relationship.targetEntity", "No target entity");
            } else if (!targetEntity.isGeneric()) {
                ValidationFailure emptyClass = BeanValidationFailure.validateNotEmpty(name,
                        "relationship.targetEntity.className", targetEntity.getClassName());
                if (emptyClass != null) {
                    return emptyClass;
                }

                ValidationFailure badClass = BeanValidationFailure.validateJavaClassName(name,
                        "relationship.targetEntity.className", targetEntity.getClassName());
                if (badClass != null) {
                    return badClass;
                }
            }
        }

        return null;
    }
}
