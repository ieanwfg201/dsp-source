package com.kritter.core.expressiontree;

import com.kritter.core.expressiontree.node.EvalNode;
import com.kritter.core.expressiontree.node.LeafNode;
import com.kritter.core.expressiontree.node.Node;
import com.kritter.core.expressiontree.utils.ExpressionHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import lombok.Getter;

/**
 * The complete expression tree.
 */
public class ExpressionTree {
    @Getter private Node root;

    public ExpressionTree(String expression, Map<String, Types> typesMap, Map<String, Integer> posMap) {
        root = parse(expression, 0, expression.length() - 1, typesMap, posMap);
    }

    /**
     * Evaluate the boolean expression represented by this tree for the given set of values.
     * @param values List containing the values for the keywords
     * @return Result of the evaluation.
     */
    public boolean evaluate(ArrayList<Object> values) {
        if(root == null)
            return false;
        return root.evaluate(values);
    }

    /**
     * Given an expression string and a beginnning and end point, parses the sub-string that starts at
     * the beginning point and ends at the end point(inclusive) and forms the expression tree
     * corresponding to that string.
     * @param expression : Expression string.
     * @param b Beginning point in the string. Must be >= 0 and < expression.length()
     * @param e End point in the string. Must be >= b and < expression.length()
     * @param typesMap For each keyword, the type of the keyword
     * @param posMap For each keyword, the position in the list corresponding to it.
     * @return Root node for the sub-string corresponding to that expression.
     */
    private Node parse(String expression, int b, int e,
                       Map<String, Types> typesMap, Map<String, Integer> posMap) {
        if(b < 0 || e >= expression.length()) return null;
        for(; b < expression.length() && Character.isWhitespace(expression.charAt(b)); ++b) ;
        for(; e >= 0 && Character.isWhitespace(expression.charAt(e)); --e) ;
        if(e <= b) return null;
        if(expression.charAt(b) != '(' && expression.charAt(e) != ')') {
            // Leaf node condition
            int pos = b;
            for(; pos < expression.length() && !Character.isWhitespace(expression.charAt(pos))
                    && !ExpressionHelper.isOperatorCharacter(expression.charAt(pos)); ++pos) ;

            String firstOperand = expression.substring(b, pos);
            if(!typesMap.containsKey(firstOperand))
                throw new RuntimeException("Malformed expression. " + firstOperand + " not defined");
            Types type = typesMap.get(firstOperand);

            while(Character.isWhitespace(expression.charAt(pos))) ++pos;
            if(pos >= e) {
                throw new RuntimeException("Malformed expression. Only one " +
                        "operand defined without any operation");
            }

            int endPos = pos;
            for(; endPos < expression.length() && !Character.isWhitespace(expression.charAt(endPos))
                    && ExpressionHelper.isOperatorCharacter(expression.charAt(endPos)); ++endPos) ;
            if(endPos >= e) {
                throw new RuntimeException("Malformed expression. Only one operand defined with " +
                        "part of the operator. " + expression.substring(b));
            }
            --endPos;
            Operators operator = ExpressionHelper.getOperator(expression, pos, endPos);

            pos = endPos + 1;
            for(; pos < expression.length() && Character.isWhitespace(expression.charAt(pos)); ++pos) ;
            if(pos > e) {
                throw new RuntimeException("Only one operand with operator defined. No second operator" +
                        "found " + expression.substring(b));
            }
            String valueStr = expression.substring(pos, e + 1);
            Object value = ExpressionHelper.getValue(valueStr, type);

            return new LeafNode(operator, type, firstOperand, value, posMap);
        } else if(expression.charAt(b) != '(' || expression.charAt(e) != ')') {
            throw new RuntimeException("Malformed expression. " + expression.substring(b, e + 1));
        }
        else {
            // Internal node condition
            int first = b;
            Operators operator = null;
            EvalNode thisNode = new EvalNode();
            int nodeCount = 0;
            while(first < e) {
                for(; first < e && Character.isWhitespace(expression.charAt(first)); ++first) ;
                int pos = first;
                // Check for operator
                for(; pos < e && ExpressionHelper.isOperatorCharacter(expression.charAt(pos)); ++pos);
                if(pos > first) {
                    // Operator found
                    --pos;
                    Operators op = ExpressionHelper.getOperator(expression, first, pos);
                    if(operator == null) {
                        operator = op;
                        thisNode.setOperator(operator);
                    }
                    else if(operator != op) {
                        throw new RuntimeException("Operators cannot be mixed. Already set " +
                        operator + ", new operator found = " + op);
                    }
                    first = pos + 1;
                } else {
                    if(expression.charAt(first) != '(')
                        throw new RuntimeException("Invalid syntax for expression " + expression);
                    int up = 0;
                    for(; pos <= e; ++pos) {
                        char ch = expression.charAt(pos);
                        if(ch == '(') ++up;
                        if(ch == ')') --up;
                        if(up == 0) break;
                    }
                    if(up != 0) throw new RuntimeException("Invalid syntax for expression " + expression);
                    Node n = parse(expression, first + 1, pos - 1, typesMap, posMap);
                    if(first == b && pos - 1 == e)
                        return n;
                    else if(n != null) {
                        thisNode.setChild(n);
                        ++nodeCount;
                    }
                    first = pos + 1;
                }
            }
            if(nodeCount == 0) 
                return null;
            return thisNode;
        }
    }

    public static void main(String[] args) {
        Map<String, Types> typesMap = new HashMap<String, Types>();
        typesMap.put("a", Types.INTEGER);
        typesMap.put("b", Types.DOUBLE);
        Map<String, Integer> posMap = new HashMap<String, Integer>();
        posMap.put("a", 0);
        posMap.put("b", 1);
        // String expression = "b = 10.5";
        // String expression = "(())";
        // String expression = "(a = 100) | (((a = 50) | (a = 60)) & (b = 1.2))";
        String expression = "a >= 0";
        // String expression = "(a = 100) | (b = 1.2) | (c != 2.3)";
        // String expression = "() & () & (a = 100)";
        try {
            ExpressionTree tree = new ExpressionTree(expression, typesMap, posMap);
            System.out.println(tree.root);
            ArrayList<Object> list = new ArrayList<Object>();
            // list.add(50);
            // list.add(1.2);
            Random random = new Random();
            // int v = random.nextInt(100);
            int v = 90;
            // System.out.println("Value = " + v);
            list.add(v);
            System.out.println(tree.evaluate(list));
        } catch (RuntimeException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }
}
