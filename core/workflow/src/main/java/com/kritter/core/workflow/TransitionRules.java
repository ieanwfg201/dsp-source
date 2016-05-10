package com.kritter.core.workflow;

import com.kritter.core.expressiontree.ExpressionTree;
import com.kritter.core.expressiontree.Types;
import com.kritter.core.expressiontree.Variable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransitionRules {
    private ArrayList<String> variables;
    private List<ExpressionTree> trees;
    private List<JobSet> jobSets;
    private JobSet defaultNextJobSet;
    private JobSet errorJobSet;

    public TransitionRules(List<Variable> variables, JobSet defaultNextJobSet, JobSet errorJobSet, List<TransitionRule> rules) {
        Map<String, Types> typesMap = new HashMap<String, Types>();
        Map<String, Integer> posMap = new HashMap<String, Integer>();
        this.variables = new ArrayList<String>();
        trees = new ArrayList<ExpressionTree>();
        jobSets = new ArrayList<JobSet>();
        this.defaultNextJobSet = defaultNextJobSet;
        this.errorJobSet = errorJobSet;

        for(Variable variable : variables) {
            String str = variable.getKeyName();
            Types type = variable.getType();
            if(typesMap.containsKey(str))
                throw new RuntimeException("Key " + str + " already declared. Duplicate found.");
            if(type == null)
                throw new RuntimeException("Type not found for key " + str);
            typesMap.put(str, type);
            posMap.put(str, this.variables.size());
            this.variables.add(str);
        }

        for(TransitionRule rule : rules) {
            String expression = rule.getExpression();
            JobSet jobSet = rule.getJobSet();
            if(expression == null || expression.isEmpty() || jobSet == null)
                continue;
            ExpressionTree tree = new ExpressionTree(expression, typesMap, posMap);
            if(tree.getRoot() == null)
                continue;
            trees.add(tree);
            jobSets.add(jobSet);
        }
    }

    public JobSet getNextJobSet(Context context) {
        ArrayList<Object> values = new ArrayList<Object>(variables.size());
        for(String variableName : variables) {
            values.add(context.getValue(variableName));
        }

        for(int i = 0; i < trees.size(); ++i) {
            ExpressionTree tree = trees.get(i);
            JobSet jobSet = jobSets.get(i);
            if(tree.evaluate(values))
                return jobSet;
        }

        return defaultNextJobSet;
    }

    public JobSet getErrorJobSet() {
        return errorJobSet;
    }
}
