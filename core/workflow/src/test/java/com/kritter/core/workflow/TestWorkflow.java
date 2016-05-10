package com.kritter.core.workflow;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class TestWorkflow {
    public static class InitJob implements Job {
        @Override
        public String getName() {
            return "Random name";
        }

        @Override
        public void execute(Context context) {
            context.setValue("gender", 100);
            context.setValue("siteId", "www.google.com");
            // System.out.println("Executing the init worker");
        }
    }

    public static class DummyJob implements Job {
        @Override
        public String getName() {
            return "Dummy Job";
        }

        @Override
        public void execute(Context context) {
            // Don't do anything.. Pass through
            // System.out.println("Executing the middle worker");
        }
    }

    public static class FinalJob implements Job {
        @Override
        public String getName() {
            return "Random name 1";
        }

        @Override
        public void execute(Context context) {
            // System.out.println("Executing the final worker");
        }
    }

    public static void main(String[] args) {
        int numTimes = 1000000;
        System.out.println(System.getProperty("user.dir"));
        if(args.length != 0) {
            numTimes = Integer.parseInt(args[0]);
        }
        // Initialize the workflow
        ApplicationContext context = new FileSystemXmlApplicationContext("core/workflow/src/test/resources/workflow.xml");
        BeanFactory factory = context;
        Workflow workflow = (Workflow) factory.getBean("Workflow");
        long beginTime = System.currentTimeMillis();
        for(int i = 0; i < numTimes; ++i)
            workflow.executeRequest(null, null);
        long endTime = System.currentTimeMillis();
        long timeTaken = endTime - beginTime;
        double averageTime = timeTaken * 1.0 / numTimes;
        System.out.println("Total time for " + numTimes + " requests = " + timeTaken);
        System.out.println("Average time = " + averageTime);
    }
}
