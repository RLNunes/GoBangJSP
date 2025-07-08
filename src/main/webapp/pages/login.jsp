<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <title>Login - Go Bang</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bulma@0.9.4/css/bulma.min.css"/>
    <%
        String ctx = request.getContextPath();
    %>
    <link rel="stylesheet" href="<%= ctx %>/css/style.css" />
</head>
<body>
<section class="custom-box">
    <h1 class="title is-3 has-text-weight-bold mb-5 has-text-centered">Login</h1>
    <%-- Mensagens de erro e sucesso (sem JSTL) --%>
    <% if (session.getAttribute("error") != null) { %>
        <div style="color:red; margin-bottom:1em;"><%= session.getAttribute("error") %></div>
        <% session.removeAttribute("error"); %>
    <% } %>
    <% if (session.getAttribute("success") != null) { %>
        <div style="color:green; margin-bottom:1em;"><%= session.getAttribute("success") %></div>
        <% session.removeAttribute("success"); %>
    <% } %>
    <form action="${pageContext.request.contextPath}/login" method="post">
        <div class="field">
            <label class="label has-text-left" for="username">Username</label>
            <div class="control">
                <input class="input" type="text" id="username" name="username" placeholder="Enter your username"
                       value="${param.username}"
                       required/>
            </div>
        </div>
        <div class="field">
            <label class="label has-text-left" for="password">Password</label>
            <div class="control">
                <input class="input" type="password" id="password" name="password" placeholder="Enter your password"
                       value="${param.password}"
                       required/>
            </div>
        </div>
        <div class="field mt-5">
            <div class="control">
                <button type="submit" class="btn btn-primary" id="loginBtn">Login</button>
            </div>
        </div>
    </form>
    <a href="../index.jsp" class="button is-link is-light mt-4 is-fullwidth">
        ← Back Home
    </a>

</section>
</body>
</html>
