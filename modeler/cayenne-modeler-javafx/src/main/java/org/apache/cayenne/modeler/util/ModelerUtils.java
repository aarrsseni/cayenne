package org.apache.cayenne.modeler.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModelerUtils {
    public static List<String> getDeleteRules(){
        return new ArrayList<>(Arrays.asList(
                "No Action",
                "Nullify",
                "Cascade",
                "Deny"
        ));
    }
}
