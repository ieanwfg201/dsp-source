package com.kritter.fanoutinfra.benchmark.compare;

import com.kritter.fanoutinfra.benchmark.apachehttpasync.ApacheHttpAsyncClient;
import com.kritter.fanoutinfra.benchmark.apachehttpcorenio.ApacheHttpCoreNIOClient;
import com.kritter.fanoutinfra.benchmark.common.BenchRunner;
import com.kritter.fanoutinfra.benchmark.common.Config;
import com.kritter.fanoutinfra.benchmark.ning.NingHttpClient;

public class Compare {
    public static void main(final String[] args) throws Exception {
        final Config config = BenchRunner.parseConfig(args);
        if (config.getUri() == null) {
            System.err.println("Please specify a target URI");
            System.exit(-1);
        }
        System.out.println("Running benchmark against " + config.getUri());
        BenchRunner.run(new ApacheHttpCoreNIOClient(), config);
        BenchRunner.run(new ApacheHttpAsyncClient(), config);
        BenchRunner.run(new NingHttpClient(), config);
    }
}



