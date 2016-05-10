<html>
<head><title>URL ALIAS MANAGER</title></head>
<body>
  <%@ page import="com.kritter.postimpression.utils.JSPUtils"%>
  <% 
     String udidParam   = "$APP_SUB";
     String userName    = request.getParameter("name");
     String password    = request.getParameter("password");
     String publisherId = request.getParameter("zone_id");

     if(null==userName || null==password || null==publisherId){
  %> 
 
  <h3>Please enter your name and the password.Also The publisher Id for which click url has to be generated.</h3>
  <%
  }
  else if(userName.equals(JSPUtils.postImpressionUtils.getJspAccessUserName()) &&
          password.equals(JSPUtils.postImpressionUtils.getJspAccessPassword()) &&
          null != publisherId){
  %>
  <h3>Enter original URL to fetch alias url value</h3>
  <form method="post" action="">
      <table>
      <tr>
          <td style="text-align:center">InputUrl</td>
          <td><input type="text" name="input_url" size="1024" /></td> 
      </tr>
      <tr>
          <td><input type="submit" value="Submit" /></td>
          <td><input type="reset" value="Reset" /></td>
      </tr>
      </table>
  </form>

  <%
       String inputUrl = request.getParameter("input_url");
       String aliasUrl = null;
       if(null!=inputUrl){
          try{
              aliasUrl = JSPUtils.aliasUrlManager.fetchOrCreateAliasURLForURL(inputUrl);
          }
          catch(Exception e){
              response.getWriter().print(e.getMessage());
          }

          String bannerId = aliasUrl.split("tclk/")[1].split(".clk")[0];
          response.getWriter().print("Alias Url Response [" + aliasUrl 
                                   + "?app_sub=" +  udidParam  
                                   + "&banner_id=" + bannerId
                                   + "&zone_id=" + publisherId
                                   + "]");
       }
  }
  %>

  <a href=""><h3>Try Again</h3></a>
</body>
</html>
