package com.kritter.core.expressiontree.node;

import com.kritter.core.expressiontree.Operators;
import com.kritter.core.expressiontree.Types;
import com.kritter.core.expressiontree.utils.ExpressionHelper;

import java.util.ArrayList;
import java.util.Map;

/**
 * Class representing a leaf node in the expression-tree. This node doesn't have any
 * children.
 * For e.g., given the expression ((a = 10) | (b = 100))
 *  The tree would be formed as,
 *           root
 *           / \
 *         /    \
 *    leaf1     leaf2
 *    The LeafNodes correspond to leaf1 and leaf2. They correspond to the "="(EQUAL) operator.
 *    There is one LeafNode each for a = 10 and b = 100.
 */
public class LeafNode implements Node {
    Operators operator;
    Types t;
    int id1;
    Object value;

    /**
     *
     * @param operator Operator for this node. The valid operators are "="(EQUAL), "!="(NOT EQUAL),
     *                 ">"(GREATER), "<"(LESSER), ">="(GREATER OR EQUAL), "<="(LESSER OR EQUAL),
     *                 "~"(CONTAINS), "!~"(NOT CONTAINS)
     * @param t Type of the operands at this level.
     * @param s1 String key inside Context object for this keyword.
     * @param value The value to be compared against. This is a static value.
     * @param map Keyword to position map in the list.
     */
    public LeafNode(Operators operator, Types t, String s1, Object value, Map<String, Integer> map) {
        this.operator = operator;
        this.t = t;
        id1 = map.get(s1);
        this.value = value;
    }

    /**
     * Evaluates the expression for the leaf node.
     * @param values Values for the different keywords.
     * @return Result of expression evaluation.
     */
    @Override
    public boolean evaluate(ArrayList<Object> values) {
        if(values.size() < id1) return false;
        if(values.get(id1) == null) return false;

        switch (operator) {
            case L :
                return ExpressionHelper.compare(values.get(id1), value, t) < 0;
            case G :
                return ExpressionHelper.compare(values.get(id1), value, t) > 0;
            case LE :
                return ExpressionHelper.compare(values.get(id1), value, t) <= 0;
            case GE :
                return ExpressionHelper.compare(values.get(id1), value, t) >= 0;
            case E :
            {
                switch (t) {
                    case STRING :
                        String s1 = (String) values.get(id1);
                        String s2 = (String) value;
                        return s1.equals(s2);
                    default :
                        return ExpressionHelper.compare(values.get(id1), value, t) == 0;
                }
            }
            case NE :
            {
                switch (t) {
                    case STRING :
                        String s1 = (String) values.get(id1);
                        String s2 = (String) value;
                        return !s1.equals(s2);
                    default :
                        return ExpressionHelper.compare(values.get(id1), value, t) != 0;
                }
            }
            case C :
            {
                switch (t) {
                    case STRING :
                        String s1 = (String) values.get(id1);
                        String s2 = (String) value;
                        return s1.contains(s2);
                    default :
                        return false;
                }
            }
            case NC :
            {
                switch (t) {
                    case STRING :
                        String s1 = (String) values.get(id1);
                        String s2 = (String) value;
                        return !s1.contains(s2);
                    default :
                        return false;
                }
            }
            default :
                return true;
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{\n").append(operator).append(", ")
            .append(t).append(", ").append(id1).append(", ")
                .append(value).append("\n}");
        return builder.toString();
    }
}
