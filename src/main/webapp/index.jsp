<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="en">

<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>Five in a Row - GoBang</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bulma@0.9.4/css/bulma.min.css" />
  <%
    String ctx = request.getContextPath();
  %>
  <link rel="stylesheet" href="<%= ctx %>/css/style.css" />
</head>

<body class="has-background-light">
<section class="custom-box">
  <h1 class="title is-1">GoBang Game!</h1>
  <nav>
    <a href="pages/login.jsp" class="btn btn-primary btn-red">Login</a>
    <a href="pages/register.jsp" class="btn btn-primary ">Register</a>
  </nav>
</section>
</body>

</html>