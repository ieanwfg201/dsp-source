package com.kritter.adserving.flow.job;

import com.kritter.core.workflow.Context;
import com.kritter.core.workflow.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Random;

/**
 * This class sets some variable in request context so
 * as to divide traffic in some percentages to different
 * instances of AdRankingSelectionJob jobs.
 */
public class TrafficAssignerToRankingJobs implements Job
{
    private Logger logger;
    private String name;
    private String randomProbabilityVariableName;
    private static Random randomProbabilityFinder = new Random();

    public TrafficAssignerToRankingJobs(
                                        String loggerName,
                                        String jobName,
                                        String randomProbabilityVariableName
                                       )
    {
        this.logger = LoggerFactory.getLogger(loggerName);
        this.name = jobName;
        this.randomProbabilityVariableName = randomProbabilityVariableName;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public void execute(Context context)
    {
        context.setValue(this.randomProbabilityVariableName,randomProbabilityFinder.nextInt(100));
    }
}
