package com.kritter.fanoutinfra.benchmark.common;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.ByteArrayOutputStream2;
import org.eclipse.jetty.util.IO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Src -> https://svn.apache.org/repos/asf/httpcomponents/benchmark/httpclient/trunk
 * @author rohan
 */

public class BenchRunner {

    public static Config parseConfig(final String[] args) throws ParseException {
        final Config config = new Config();
        if (args.length > 0) {
            final Option kopt = new Option("k", false, "Enable the HTTP KeepAlive feature, " +
                    "i.e., perform multiple requests within one HTTP session. " +
                    "Default is no KeepAlive");
            kopt.setRequired(false);
            final Option copt = new Option("c", true, "Concurrency while performing the " +
                    "benchmarking session. The default is to just use a single thread/client");
            copt.setRequired(false);
            copt.setArgName("concurrency");

            final Option nopt = new Option("n", true, "Number of requests to perform for the " +
                    "benchmarking session. The default is to just perform a single " +
                    "request which usually leads to non-representative benchmarking " +
                    "results");
            nopt.setRequired(false);
            nopt.setArgName("requests");

            final Option lopt = new Option("l", true, "Request content length");
            nopt.setRequired(false);
            nopt.setArgName("content length");

            final Options options = new Options();
            options.addOption(kopt);
            options.addOption(nopt);
            options.addOption(copt);
            options.addOption(lopt);

            final CommandLineParser parser = new PosixParser();
            final CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption('h')) {
                final HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("Benchmark [options]", options);
                System.exit(1);
            }
            if (cmd.hasOption('k')) {
                config.setKeepAlive(true);
            }
            if (cmd.hasOption('c')) {
                final String s = cmd.getOptionValue('c');
                try {
                    config.setConcurrency(Integer.parseInt(s));
                } catch (final NumberFormatException ex) {
                    System.err.println("Invalid number for concurrency: " + s);
                    System.exit(-1);
                }
            }
            if (cmd.hasOption('n')) {
                final String s = cmd.getOptionValue('n');
                try {
                    config.setRequests(Integer.parseInt(s));
                } catch (final NumberFormatException ex) {
                    System.err.println("Invalid number of requests: " + s);
                    System.exit(-1);
                }
            }
            if (cmd.hasOption('l')) {
                final String s = cmd.getOptionValue('l');
                try {
                    config.setContentLength(Integer.parseInt(s));
                } catch (final NumberFormatException ex) {
                    System.err.println("Invalid content length: " + s);
                    System.exit(-1);
                }
            }
            final String[] cmdargs = cmd.getArgs();
            if (cmdargs.length > 0) {
                try {
                    config.setUri(new URI(cmdargs[0]));
                } catch (final URISyntaxException ex) {
                    System.err.println("Invalid request URL : " + cmdargs[0]);
                    System.exit(-1);
                }
            }

        } else {
            config.setKeepAlive(true);
            config.setRequests(1000000);
            config.setConcurrency(50);
        }
        return config;
    }

    public static void run(final HttpAgent agent, final Config config) throws Exception {
        final SelectChannelConnector connector = new SelectChannelConnector();
        final Server server = new Server();server.addConnector(connector);
        server.setHandler(new RandomDataHandler());

        server.start();
        final int port = connector.getLocalPort();

        final byte[] content = new byte[config.getContentLength()];
        final int r = Math.abs(content.hashCode());
        for (int i = 0; i < content.length; i++) {
            content[i] = (byte) ((r + i) % 96 + 32);
        }

        final URI warmup = new URI("http", null, "localhost", port, "/rnd", "c=2048", null);
        final URI target = new URI("http", null, "localhost", port, "/echo", null, null);

        try {
            try {
                agent.init();
                // Warm up
                agent.get(warmup, 500, 2);
                // Sleep a little
                Thread.sleep(5000);

                System.out.println("=================================");
                System.out.println("HTTP agent: " + agent.getClientName());
                System.out.println("---------------------------------");
                System.out.println(config.getRequests() + " POST requests");
                System.out.println("---------------------------------");

                final long startTime = System.currentTimeMillis();
                final Stats stats = agent.post(target, content, config.getRequests(), config.getConcurrency());
                final long finishTime = System.currentTimeMillis();
                Stats.printStats(target, startTime, finishTime, stats);
            } finally {
                agent.shutdown();
            }
            System.out.println("---------------------------------");
        } finally {
            server.stop();
        }
        server.join();
    }

    static class RandomDataHandler extends AbstractHandler {

        public RandomDataHandler() {
            super();
        }

        @Override
        public void handle(
                final String target,
                final Request baseRequest,
                final HttpServletRequest request,
                final HttpServletResponse response) throws IOException, ServletException {
            if (target.equals("/rnd")) {
                rnd(request, response);
            } else if (target.equals("/echo")) {
                echo(request, response);
            } else {
                response.setStatus(HttpStatus.NOT_FOUND_404);
                final Writer writer = response.getWriter();
                writer.write("Target not found: " + target);
                writer.flush();
            }
        }

        private void rnd(
                final HttpServletRequest request,
                final HttpServletResponse response) throws IOException {
            int count = 100;
            final String s = request.getParameter("c");
            try {
                count = Integer.parseInt(s);
            } catch (final NumberFormatException ex) {
                response.setStatus(500);
                final Writer writer = response.getWriter();
                writer.write("Invalid query format: " + request.getQueryString());
                writer.flush();
                return;
            }

            response.setStatus(200);
            response.setContentLength(count);

            final OutputStream outstream = response.getOutputStream();
            final byte[] tmp = new byte[1024];
            final int r = Math.abs(tmp.hashCode());
            int remaining = count;
            while (remaining > 0) {
                final int chunk = Math.min(tmp.length, remaining);
                for (int i = 0; i < chunk; i++) {
                    tmp[i] = (byte) ((r + i) % 96 + 32);
                }
                outstream.write(tmp, 0, chunk);
                remaining -= chunk;
            }
            outstream.flush();
        }

        private void echo(
                final HttpServletRequest request,
                final HttpServletResponse response) throws IOException {

            final ByteArrayOutputStream2 buffer = new ByteArrayOutputStream2();
            final InputStream instream = request.getInputStream();
            if (instream != null) {
                IO.copy(instream, buffer);
                buffer.flush();
            }
            final byte[] content = buffer.getBuf();
            final int len = buffer.getCount();

            response.setStatus(200);
            response.setContentLength(len);

            final OutputStream outstream = response.getOutputStream();
            outstream.write(content, 0, len);
            outstream.flush();
        }

    }

}
