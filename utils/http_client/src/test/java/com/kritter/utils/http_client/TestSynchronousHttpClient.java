package com.kritter.utils.http_client;

import com.kritter.utils.http_client.entity.HttpRequest;
import com.kritter.utils.http_client.entity.HttpResponse;
import org.junit.Test;

/**
 * This class is used for testing out SynchronousHttpClient by
 * means of firing requests to few known domains and getting
 * back successful response.
 */
public class TestSynchronousHttpClient
{
    @Test
    public void getSuccessfulResponseFromValidThirdPartyServer()
    {
        SynchronousHttpClient synchronousHttpClient = new SynchronousHttpClient("",20);

        HttpRequest httpRequest = new HttpRequest("http://kritter.in",100,50, HttpRequest.REQUEST_METHOD.GET_METHOD,null,null);

        System.out.println("Valid http call results...");
        System.out.println("status: " + synchronousHttpClient.fetchResponseFromThirdPartyServer(httpRequest).getResponseStatusCode());
        System.out.println("payload: " + synchronousHttpClient.fetchResponseFromThirdPartyServer(httpRequest).getResponsePayload());
    }

    @Test
    public void getNotFoundResponseFromInvalidThirdPartyServer()
    {
        SynchronousHttpClient synchronousHttpClient = new SynchronousHttpClient("",20);

        HttpRequest httpRequest = new HttpRequest("http://randomexistingsite.com/",100,50,
                                                  HttpRequest.REQUEST_METHOD.GET_METHOD,null,null);

        System.out.println("Invalid http call results ...");
        System.out.println("status: " + synchronousHttpClient.fetchResponseFromThirdPartyServer(httpRequest).getResponseStatusCode());
        System.out.println("payload: " + synchronousHttpClient.fetchResponseFromThirdPartyServer(httpRequest).getResponsePayload());
    }

    public static void main(String s[])
    {
        SynchronousHttpClient t = new SynchronousHttpClient("",20);
        HttpRequest req1 = new HttpRequest("http://kritter.in",10000,5000, HttpRequest.REQUEST_METHOD.GET_METHOD,null,null);
        HttpResponse res1 = t.fetchResponseFromThirdPartyServer(req1);

        HttpRequest req2 = new HttpRequest("http://randomnonexistingsite.in",100,50,
                HttpRequest.REQUEST_METHOD.GET_METHOD,null,null);
        HttpResponse res2 = t.fetchResponseFromThirdPartyServer(req2);

        System.out.println("valid response code: " + res1.getResponseStatusCode() +
                " and payload: " + res1.getResponsePayload());

        System.out.println("invalid response code: " + res2.getResponseStatusCode() +
                " and payload: " + res2.getResponsePayload());
    }
}
