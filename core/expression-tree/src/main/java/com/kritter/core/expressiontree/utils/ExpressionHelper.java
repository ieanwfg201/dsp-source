package com.kritter.core.expressiontree.utils;

import com.kritter.core.expressiontree.Operators;
import com.kritter.core.expressiontree.Types;

/**
 * Utility functions for expression tree.
 */
public class ExpressionHelper {
    public static String STRING_TYPE_NAME = "string";
    public static String INTEGER_TYPE_NAME = "integer";
    public static String LONG_TYPE_NAME = "long";
    public static String FLOAT_TYPE_NAME = "float";
    public static String DOUBLE_TYPE_NAME = "double";
    public static String BOOLEAN_TYPE_NAME = "boolean";

    /**
     * Compares 2 objects of given type and returns the result of the comparison.
     * The result is the same as the one given by standard comparator for the corresponding
     * objects. Types supported are Boolean, Integer, Long, Float, Double.
     * @param a1
     * @param a2
     * @param t
     * @return
     */
    public static int compare(Object a1, Object a2, Types t) {
        switch (t) {
            case BOOLEAN :
                Boolean b1 = (Boolean) a1;
                Boolean b2 = (Boolean) a2;
                return b1.compareTo(b2);
            case INTEGER :
                Integer i1 = (Integer) a1;
                Integer i2 = (Integer) a2;
                return i1.compareTo(i2);
            case LONG :
                Long l1 = (Long) a1;
                Long l2 = (Long) a2;
                return l1.compareTo(l2);
            case FLOAT :
                Float f1 = (Float) a1;
                Float f2 = (Float) a2;
                return f1.compareTo(f2);
            case DOUBLE :
                Double d1 = (Double) a1;
                Double d2 = (Double) a2;
                return d1.compareTo(d2);
            default :
                return 0;
        }
    }

    /**
     * Whether the character corresponds to one of those reserved for our operators.
     * @param ch
     * @return
     */
    public static boolean isOperatorCharacter(char ch) {
        return ch == '>' || ch == '<' || ch == '=' || ch == '!' || ch == '~' || ch == '&' || ch == '|';
    }

    /**
     * Extracts out the value of the given type from the string.
     * @param s
     * @param t
     * @return
     */
    public static Object getValue(String s, Types t) {
        switch (t) {
            case STRING :
                return s;
            case INTEGER :
                return Integer.parseInt(s);
            case LONG :
                return Long.parseLong(s);
            case BOOLEAN :
                return Boolean.parseBoolean(s);
            case FLOAT :
                return Float.parseFloat(s);
            case DOUBLE :
                return Double.parseDouble(s);
            default :
                return null;
        }
    }

    /**
     * Given a string and a beginning and end point, extract out the sub-string inclusive
     * of the end points, and return the operator represented by the sub-string.
     * Throw a run time exception if the sub-string doesn't represent any operator.
     * @param s Expression string
     * @param b Beginning point
     * @param e End point
     * @return Operator
     */
    public static Operators getOperator(String s, int b, int e) {
        if(e < b) return null;
        if(e == b) {
            char c = s.charAt(b);
            if(c == '>') return Operators.G;
            if(c == '<') return Operators.L;
            if(c == '=') return Operators.E;
            if(c == '~') return Operators.C;
            if(c == '&') return Operators.AND;
            if(c == '|') return Operators.OR;
            throw new RuntimeException("Invalid operator character " + c);
        }
        if(e == b + 1) {
            char c0 = s.charAt(b);
            char c1 = s.charAt(e);
            if(c0 == '<') {
                if(c1 == '=') return Operators.LE;
                throw new RuntimeException("Invalid operator characters " + c0 + c1);
            }
            if(c0 == '>') {
                if(c1 == '=') return Operators.GE;
                throw new RuntimeException("Invalid operator characters " + c0 + c1);
            }
            if(c0 == '!') {
                if(c1 == '=') return Operators.NE;
                if(c1 == '~') return Operators.NC;
                throw new RuntimeException("Invalid operator characters " + c0 + c1);
            }
            throw new RuntimeException("Invalid operator " + c0 + c1);
        }
        throw new RuntimeException("Invalid operator length. Cannot be greater than 2. " +
                "Operator supplied is " + s.substring(b, e + 1));
    }

    /**
     * From type name, return Type object. Returns the type corresponding to the type name.
     * The check is case insensitive.
     * Type name must be one of
     *     "string",
     *     "integer",
     *     "long",
     *     "float",
     *     "double",
     *     "boolean",
     *     or their case variants
     *
     * @param typeName Name of the type
     * @return Type object
     */
    public static Types getType(String typeName) {
        if(typeName == null)
            return null;

        if(typeName.equalsIgnoreCase(STRING_TYPE_NAME))
            return Types.STRING;
        if(typeName.equalsIgnoreCase(INTEGER_TYPE_NAME))
            return Types.INTEGER;
        if(typeName.equalsIgnoreCase(LONG_TYPE_NAME))
            return Types.LONG;
        if(typeName.equalsIgnoreCase(FLOAT_TYPE_NAME))
            return Types.FLOAT;
        if(typeName.equalsIgnoreCase(DOUBLE_TYPE_NAME))
            return Types.DOUBLE;
        if(typeName.equalsIgnoreCase(BOOLEAN_TYPE_NAME))
            return Types.BOOLEAN;
        return null;
    }
}
