<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="org.rafael.gobangjsp.common.records.UserProfileData" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <title>Profile - 5 in a Row</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bulma@0.9.4/css/bulma.min.css"/>
    <%
        String ctx = request.getContextPath();
    %>
    <link rel="stylesheet" href="<%= ctx %>/css/style.css"/>
</head>
<body>
<section class="custom-box">
    <h1 class="title mb-4">Edit Profile</h1>
    <form action="<%= ctx%>/edit" method="post" enctype="multipart/form-data">
        <%
            UserProfileData profile = (UserProfileData) session.getAttribute("userProfile");
        %>
        <div class="field">
            <label class="label has-text-left" for="username">Username</label>
            <div class="control">
                <input class="input" type="text" id="username" name="username"
                       value="<%= profile != null ? profile.username() : "" %>"
                       required/>
            </div>
        </div>
        <div class="field">
            <label class="label has-text-left" for="password">Password</label>
            <div class="control">
                <input class="input" type="password" id="password" name="password" placeholder="Change password"/>
            </div>
        </div>
        <div class="field">
            <label class="label has-text-left" for="nationality">Nationality</label>
            <div class="control">
                <input class="input" type="text" id="nationality" name="nationality"
                       value="<%= profile != null ? profile.nationality() : "" %>"/>
            </div>
        </div>
        <div class="field">
            <label class="label has-text-left" for="age">Age</label>
            <div class="control">
                <input class="input" type="number" id="age" name="age"
                       value="<%= profile != null ? profile.age() : "" %>" min="1"
                       max="120"/>
            </div>
        </div>
        <div class="field">
            <label class="label has-text-left" for="bgcolor">Preferred Background Color</label>
            <div class="control">
                <input class="input" type="color" id="bgcolor" name="bgcolor"
                       value="<%= request.getAttribute("bgcolor") != null ? request.getAttribute("bgcolor") : "#a3d2ca" %>"/>
            </div>
        </div>
        <div class="field">
            <label class="label has-text-left" for="photo">Profile Photo</label>
            <div class="control">
                <input class="input" type="file" id="photo" name="photo" accept="image/*"/>
            </div>
        </div>
        <div class="field is-grouped is-grouped-right mt-4">
            <div class="control">
                <a href="pages/dashboard.jsp" class="button is-link is-light">← Back to Dashboard</a>
                <button class="button is-link" type="submit">Save Changes</button>
            </div>
        </div>
        <% if (request.getAttribute("error") != null) { %>
        <div class="notification is-danger mt-3"><%= request.getAttribute("error") %>
        </div>
        <% } %>
        <% if (request.getAttribute("success") != null) { %>
        <div class="notification is-success mt-3"><%= request.getAttribute("success") %>
        </div>
        <% } %>
    </form>
</section>
</body>
</html>
