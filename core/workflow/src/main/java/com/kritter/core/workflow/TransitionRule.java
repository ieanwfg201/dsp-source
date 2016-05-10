package com.kritter.core.workflow;

import lombok.Getter;

@Getter
public class TransitionRule {
    private String expression;
    private JobSet jobSet;

    public TransitionRule(String expression, JobSet jobSet) {
        this.expression = expression;
        this.jobSet = jobSet;
    }
}
