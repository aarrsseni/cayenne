package org.apache.cayenne.modeler.jFx.converters;

import org.apache.cayenne.map.DeleteRule;

public class ComboBoxStringIntegerConverter implements ComboBoxCellConverter<Integer> {
    @Override
    public Integer toItem(String s) {
        return DeleteRule.deleteRuleForName(s);
    }

    @Override
    public String fromItem(Integer s) {
        return DeleteRule.deleteRuleName(s);
    }
}
