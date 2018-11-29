package org.apache.cayenne.modeler.services;

import org.apache.cayenne.map.Embeddable;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.validation.ValidationResult;

public interface CgenValidationService {

    void validateEmbeddable(ValidationResult validationBuffer, Embeddable embeddable);

    void validateEntity(ValidationResult validationBuffer, ObjEntity entity, boolean clientValidation);

}
