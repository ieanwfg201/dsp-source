package com.kritter.core.workflow;

import java.util.List;

import com.kritter.core.metrics.YammerMetrics;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JobSets are executed by the Workflow one by one. The workflow is similar to a flow
 * chart in which the jobsets are individual activities and also provide the decision as
 * to which jobset to execute next.
 * Each jobset also consists of a list of jobs. The jobs are executed in a sequential fashion.
 */
public class JobSet
{
    @Getter private String name;
    private TransitionRules rules;
    private List<Job> jobs;

    public static String WORKFLOW_LOGGER_NAME = "workflow.logger";
    private Logger workflowLogger;

    public JobSet(String name, TransitionRules rules, List<Job> jobs)
    {
        this.name = name;
        this.rules = rules;
        this.jobs = jobs;
        this.workflowLogger = LoggerFactory.getLogger(WORKFLOW_LOGGER_NAME);
    }

    /**
     * Executes the list of jobs sequentially. If any of the jobs in the set fails,
     * skips the rest of the jobs and returns false.
     *
     * @param context Application context
     * @return true if all the jobs succeeded, false if any of the jobs fail
     */
    public boolean execute(Context context, YammerMetrics metrics)
    {
        for(Job job : jobs)
        {
            long beginTime = System.nanoTime();

            metrics.incrementJobInvocations(job.getName());

            try
            {
                if(null != context)
                {
                    job.execute(context);
                    if (context.isTerminated())
                        return false;
                }
                else
                {
                    workflowLogger.error("Context is null while trying to execute job: {} ", job.getName());
                    return false;
                }
            }
            catch (Exception e)
            {
                metrics.incrementJobFailures(job.getName());

                workflowLogger.error("Job: {} execution failed due to some exception in execute method of the job.",
                                      job.getName());

                workflowLogger.error("Exception inside executing job ",e);

                return false;
            }
            finally
            {
                long endTime = System.nanoTime();
                // Update job latency to the nearest microsecond.
                metrics.incrementJobLatency(job.getName(), (endTime - beginTime + 500) / 1000);
            }
        }

        return true;
    }

    public JobSet getNextJob(Context context) {
        if(rules == null)
            return null;
        return rules.getNextJobSet(context);
    }

    public JobSet getErrorJobSet() {
        if(rules == null)
            return null;
        return rules.getErrorJobSet();
    }
}
