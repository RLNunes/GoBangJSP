<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1" %>
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
        <% if (session.getAttribute("nickname") != null) { %>
        Welcome, <%= session.getAttribute("nickname") %>!
        <% } else { %>
        Welcome, Player!
        <% } %>
    </h1>
    <a href="game.html" class="btn btn-primary">Play</a>
    <a href="${pageContext.request.contextPath}/profile" class="btn btn-primary">Profile</a>
    <a href="ranking.html" class="btn btn-primary">Hall of Fame</a>
    <a href="login.html" class="btn btn-red">Logout</a>
    <a href="../index.html" class="btn btn-green">← Back Home</a>
</div>
</body>
</html>
