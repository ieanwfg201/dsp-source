package com.kritter.fanoutinfra.benchmark.apachehttpcorenio;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.concurrent.Semaphore;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.nio.DefaultHttpClientIODispatch;
import org.apache.http.impl.nio.pool.BasicNIOConnPool;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.ContentEncoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.entity.EntityAsyncContentProducer;
import org.apache.http.nio.entity.HttpAsyncContentProducer;
import org.apache.http.nio.entity.NByteArrayEntity;
import org.apache.http.nio.protocol.BasicAsyncRequestProducer;
import org.apache.http.nio.protocol.HttpAsyncRequestExecutor;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.nio.protocol.HttpAsyncRequester;
import org.apache.http.nio.protocol.HttpAsyncResponseConsumer;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOEventDispatch;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpProcessorBuilder;
import org.apache.http.protocol.RequestConnControl;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.apache.http.util.VersionInfo;

import com.kritter.fanoutinfra.benchmark.common.BenchRunner;
import com.kritter.fanoutinfra.benchmark.common.Config;
import com.kritter.fanoutinfra.benchmark.common.HttpAgent;
import com.kritter.fanoutinfra.benchmark.common.Stats;
/**
 * Src -> https://svn.apache.org/repos/asf/httpcomponents/benchmark/httpclient/trunk
 * @author rohan
 */

public class ApacheHttpCoreNIOClient implements HttpAgent {

    private final ConnectingIOReactor ioreactor;
    private final BasicNIOConnPool pool;
    private final HttpProcessor httpproc;
    private final ConnectionReuseStrategy connReuseStrategy;

    private final IOEventDispatch ioEventDispatch;

    public ApacheHttpCoreNIOClient() throws Exception {
        super();
        final IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
                .setSoTimeout(15000)
                .build();
        final ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setBufferSize(8 * 1024)
                .setFragmentSizeHint(8 * 1024)
                .build();

        this.ioreactor = new DefaultConnectingIOReactor(ioReactorConfig);
        this.pool = new BasicNIOConnPool(ioreactor, connectionConfig);
        this.httpproc = HttpProcessorBuilder.create()
                .add(new RequestContent())
                .add(new RequestTargetHost())
                .add(new RequestConnControl())
                .add(new RequestUserAgent("Test/1.1"))
                .build();
        this.connReuseStrategy = DefaultConnectionReuseStrategy.INSTANCE;
        this.ioEventDispatch = new DefaultHttpClientIODispatch(
                new HttpAsyncRequestExecutor(),
                ConnectionConfig.DEFAULT);
    }

    @Override
    public void init() {
        final Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    ioreactor.execute(ioEventDispatch);
                } catch (final InterruptedIOException ex) {
                    System.err.println("Interrupted");
                } catch (final IOException e) {
                    System.err.println("I/O error: " + e.getMessage());
                }
            }

        });
        t.start();
    }

    @Override
    public void shutdown() throws Exception {
        this.pool.shutdown(2000);
    }

    Stats execute(final URI targetURI, final byte[] content, final int n, final int c) throws Exception {
        this.pool.setDefaultMaxPerRoute(c);
        this.pool.setMaxTotal(2000);
        final Stats stats = new Stats(n, c);

        final String scheme = targetURI.getScheme();
        final String hostname = targetURI.getHost();
        int port = targetURI.getPort();
        if (port == -1) {
            if (scheme.equalsIgnoreCase("http")) {
                port = 80;
            } else if (scheme.equalsIgnoreCase("https")) {
                port = 443;
            }
        }
        final HttpHost target = new HttpHost(hostname, port, scheme);
        final HttpAsyncRequester requester = new HttpAsyncRequester(httpproc, connReuseStrategy);
        final Semaphore semaphore = new Semaphore(c);
        for (int i = 0; i < n; i++) {

            final HttpRequest request;
            if (content == null) {
                request = RequestBuilder.get()
                        .setUri(targetURI)
                        .build();
            } else {
                request = RequestBuilder.post()
                        .setUri(targetURI)
                        .setEntity(new NByteArrayEntity(content))
                        .build();
            }

            semaphore.acquire();
            requester.execute(
                    new BasicAsyncRequestProducer(target, request),
                    new BenchmarkResponseConsumer(stats),
                    pool,
                    new BasicHttpContext(),
                    new FutureCallback<Void>() {

                        @Override
                        public void completed(final Void result) {
                            semaphore.release();
                        }

                        @Override
                        public void failed(final Exception ex) {
                            semaphore.release();
                        }

                        @Override
                        public void cancelled() {
                            semaphore.release();
                        }

                    });
        }

        stats.waitFor();
        return stats;
    }

    static class BenchmarkRequestProducer implements HttpAsyncRequestProducer {

        final HttpHost target;
        final HttpRequest request;
        final HttpAsyncContentProducer contentProducer;

        BenchmarkRequestProducer(
                final HttpHost target,
                final HttpRequest request) {
            super();
            this.target = target;
            this.request = request;
            if (request instanceof HttpEntityEnclosingRequest) {
                final HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();
                if (entity != null) {
                    if (entity instanceof HttpAsyncContentProducer) {
                        this.contentProducer = (HttpAsyncContentProducer) entity;
                    } else {
                        this.contentProducer = new EntityAsyncContentProducer(entity);
                    }
                } else {
                    this.contentProducer = null;
                }
            } else {
                this.contentProducer = null;
            }
        }

        @Override
        public void close() throws IOException {
            if (contentProducer != null) {
                contentProducer.close();
            }
        }

        @Override
        public HttpHost getTarget() {
            return target;
        }

        @Override
        public HttpRequest generateRequest() throws IOException, HttpException {
            return request;
        }

        @Override
        public void produceContent(
                final ContentEncoder encoder, final IOControl ioctrl) throws IOException {
            if (contentProducer != null) {
                contentProducer.produceContent(encoder, ioctrl);
                if (encoder.isCompleted()) {
                    contentProducer.close();
                }
            }
        }

        @Override
        public void requestCompleted(final HttpContext context) {
        }

        @Override
        public void failed(final Exception ex) {
        }

        @Override
        public boolean isRepeatable() {
            return contentProducer == null || contentProducer.isRepeatable();
        }

        @Override
        public void resetRequest() throws IOException {
            if (contentProducer != null) {
                contentProducer.close();
            }
        }

    };

    static class BenchmarkResponseConsumer implements HttpAsyncResponseConsumer<Void> {

        private final Stats stats;

        private ByteBuffer bbuf;
        private int status;
        private long contentLen = 0;
        private Exception ex;
        private boolean done = false;

        BenchmarkResponseConsumer(final Stats stats) {
            super();
            this.stats = stats;
        }

        @Override
        public void close() throws IOException {
            if (!this.done) {
                this.done = true;
                this.stats.failure(contentLen);
            }
            bbuf = null;
        }

        @Override
        public boolean cancel() {
            bbuf = null;
            return false;
        }

        @Override
        public void responseReceived(
                final HttpResponse response) throws IOException, HttpException {
            this.status = response.getStatusLine().getStatusCode();
        }

        @Override
        public void consumeContent(
                final ContentDecoder decoder, final IOControl ioctrl) throws IOException {
            if (this.bbuf == null) {
                this.bbuf = ByteBuffer.allocate(4096);
            }
            for (;;) {
                final int bytesRead = decoder.read(this.bbuf);
                if (bytesRead <= 0) {
                    break;
                }
                this.contentLen += bytesRead;
                this.bbuf.clear();
            }
        }

        @Override
        public void responseCompleted(final HttpContext context) {
        }

        @Override
        public void failed(final Exception ex) {
            this.ex = ex;
        }

        @Override
        public Exception getException() {
            return this.ex;
        }

        @Override
        public Void getResult() {
            if (this.status == 200 && this.ex == null) {
                stats.success(contentLen);
            } else {
                stats.failure(contentLen);
            }
            this.done = true;
            return null;
        }

        @Override
        public boolean isDone() {
            return this.done;
        }

    };

    @Override
    public Stats get(final URI target, final int n, final int c) throws Exception {
        return execute(target, null, n, c);
    }

    @Override
    public Stats post(final URI target, final byte[] content, final int n, final int c) throws Exception {
        return execute(target, content, n, c);
    }

    @Override
    public String getClientName() {
        final VersionInfo vinfo = VersionInfo.loadVersionInfo("org.apache.http",
                Thread.currentThread().getContextClassLoader());
        return "HttpCore NIO (ver: " +
            ((vinfo != null) ? vinfo.getRelease() : VersionInfo.UNAVAILABLE) + ")";
    }

    public static void main(final String[] args) throws Exception {
        final Config config = BenchRunner.parseConfig(args);
        BenchRunner.run(new ApacheHttpCoreNIOClient(), config);
    }
}

