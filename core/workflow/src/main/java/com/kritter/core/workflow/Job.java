package com.kritter.core.workflow;

/**
 * The basic job interface. Defines the basic functionality that has to be exposed
 * by a job. A job is the unit of work. Each functionality has to be exposed via a job.
 * The job must be stateless, i.e., shouldn't change state for a request.
 */
public interface Job {
    public String getName();

    /**
     * The actual method where the work gets done
     * @param context Request context
     */
    public void execute(Context context);
}
