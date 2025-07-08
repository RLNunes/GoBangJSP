<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <title>Player Dashboard</title>
    <link type="text/css" rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bulma@0.9.4/css/bulma.min.css"/>
    <%
        String ctx = request.getContextPath();
    %>
    <link rel="stylesheet" href="<%= ctx %>/css/style.css"/>
</head>
<body>
<div class="custom-box">
    <h1 class="title">
        <% if (session.getAttribute("username") != null) { %>
        Welcome, <%= session.getAttribute("username") %>!
        <% } else { %>
        Welcome, Player!
        <% } %>
    </h1>
    <a href="game.html" class="btn btn-primary">Play</a>
    <a href="<%= ctx %>/profile" class="btn btn-primary">Profile</a>
    <a href="<%= ctx %>/ranking" class="btn btn-primary">Hall of Fame</a>
    <a href="<%= ctx %>/logout" class="btn btn-red">Logout</a>
</div>
</body>
</html>
