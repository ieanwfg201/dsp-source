package com.kritter.core.expressiontree;

import com.kritter.core.expressiontree.utils.ExpressionHelper;

import lombok.Getter;

/**
 * A key name with type. For e.g., in the expression
 * a = 100
 * a is the key name and type could be integer or long.
 */
@Getter
public class Variable {
    private String keyName;
    private Types type;

    public Variable(String keyName, String typeName) {
        this.keyName = keyName;
        this.type = ExpressionHelper.getType(typeName);
    }
}
