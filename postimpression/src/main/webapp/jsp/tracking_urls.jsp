<html>
<head><title>TRACKING URLS</title></head>
<body>
  <%@ page import="com.kritter.postimpression.utils.JSPUtils"%>
  <% 
     String userName = request.getParameter("name");
     String password = request.getParameter("password");
     if(null==userName || null==password){
  %> 
 
  <h3>Please enter your name and the password.</h3>
  <%
  }
  else if(userName.equals(JSPUtils.postImpressionUtils.getJspAccessUserName()) &&
          password.equals(JSPUtils.postImpressionUtils.getJspAccessPassword())){
  %>
  <h3>Tracking URLs Details</h3>
  <%
       String content = null;
       try
       {
           content = JSPUtils.trackingUrlFromThirdPartyReader.fetchTrackingUrlsForThirdParties();
       }
       catch(Exception e){
           response.getWriter().print(e.getMessage());
       }
          response.getWriter().print(content);
       }
  %>

  <a href=""><h3>Try Again</h3></a>
</body>
</html>
