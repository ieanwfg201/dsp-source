package com.kritter.core.expressiontree.node;

import com.kritter.core.expressiontree.Operators;

import java.util.ArrayList;

/**
 * Class representing an inner node in the expression-tree. This node must have 2 or
 * more other nodes as children.
 * For e.g., given the expression ((a = 10) | (b = 100))
 *  The tree would be formed as,
 *           root
 *           / \
 *         /    \
 *    leaf1     leaf2
 *    The root is corresponding to the "|"(OR) operator. This is an EvalNode.
 */
public class EvalNode implements Node {
    Operators operator;
    ArrayList<Node> children;

    public EvalNode() {
        this.operator = null;
        this.children = new ArrayList<Node>();
    }

    /**
     * @param operator : Operator for this particular node. The only operators valid for
     *                 internal node are &(AND) and |(OR)
     */
    public void setOperator(Operators operator) {
        this.operator = operator;
    }

    /**
     * @param n : Add a node to the list of children.
     */
    public void setChild(Node n) {
        children.add(n);
    }

    /**
     * Evaluates the expression that is rooted at this node in the tree, i.e., the expression
     * represented by the sub-tree for which this node is the root.
     * @param values The values of keywords
     * @return true or false, depending on the values. If there are no children for this node
     *          it will always return true.
     */
    @Override
    public boolean evaluate(ArrayList<Object> values) {
        if(children == null) return true;
        boolean res;

        switch (operator) {
            case AND:
                res = true;
                for(Node n : children)
                    res = res && n.evaluate(values);
                return res;
            case OR :
                res = false;
                for(Node n : children)
                    res = res || n.evaluate(values);
                return res;
            default :
                res = false;
                return res;
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{\n").append(operator).append("\n");
        if(children != null)
            for(Node n : children)
                builder.append(n).append("\n");
        builder.append("}");
        return builder.toString();
    }
}
