<html>
<head><title>Third Party Publisher Info</title></head>
<body>
  <%@ page import="com.kritter.postimpression.utils.JSPUtils"%>
  <% 
     String userName    = request.getParameter("name");
     String password    = request.getParameter("password");

     if(null==userName || null==password){
  %> 
 
  <h3>Please Enter username and password to access the page.</h3>
  <%
  }
  else if(userName.equals(JSPUtils.postImpressionUtils.getJspAccessUserName()) &&
          password.equals(JSPUtils.postImpressionUtils.getJspAccessPassword())){

          String publisherInfo = null;
    
          try{
              publisherInfo = JSPUtils.aliasUrlManager.fetchAffiliatePublisherInfo();
          }
          catch(Exception e){
              publisherInfo = "Database Error!!! " + e.getMessage();
          }

          response.getWriter().print("<h1>Third Party Publisher Info </h1><br>" + publisherInfo);
  }
  %>

  <a href=""><h3>Try Again</h3></a>
</body>
</html>
