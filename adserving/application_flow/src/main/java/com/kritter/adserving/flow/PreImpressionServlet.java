package com.kritter.adserving.flow;

import com.kritter.core.workflow.Workflow;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * This class receives pre-impression request and performs required workflow.
 * Some absolutely necessary initialization is done in this class.
 */
public class PreImpressionServlet extends HttpServlet
{
    @Override
    public void init(ServletConfig servletConfig) throws ServletException
    {
        super.init(servletConfig);
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        executeWorkflow(req,resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        executeWorkflow(req,resp);
    }


    private void executeWorkflow(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        Workflow workflow = (Workflow)getServletContext().getAttribute("workflow");
        String requestUri = req.getRequestURI();
        if(null != requestUri && requestUri.endsWith("/stats"))
        {
            writeResponseToUser(resp,workflow.getStats());
        }
        else
            workflow.executeRequest(req,resp);
    }

    private void writeResponseToUser(HttpServletResponse httpServletResponse,String content) throws IOException
    {
        OutputStream os = httpServletResponse.getOutputStream();

        if(null == content)
            content = "NO_STATS_AVAILABLE";

        os.write(content.getBytes());
        os.flush();
        os.close();
    }

    @Override
    public void destroy()
    {
    }
}
