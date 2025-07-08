<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="org.rafael.gobangjsp.common.UserProfileData" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>Hall of Fame - Go Bang</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bulma@0.9.4/css/bulma.min.css" />
  <%
    String ctx = request.getContextPath();
  %>
  <link rel="stylesheet" href="<%= ctx %>/css/style.css" />
</head>
<body>
<section class="custom-box">
  <h1 class="title">Hall of Fame</h1>
  <table class="table striped hoverable">
    <thead>
    <tr>
      <th>Rank</th>
      <th>Photo</th>
      <th>Username</th>
      <th>Nationality</th>
      <th>Wins</th>
      <th>Avg Game Time</th>
    </tr>
    </thead>
    <tbody>
    <%
      List<UserProfileData> ranking = (List<UserProfileData>) request.getAttribute("gameHistory");
      if (ranking != null) {
        int pos = 1;
        for (UserProfileData user : ranking) {
    %>
    <tr>
      <td><%= pos++ %></td>
      <td>
        <% if (user.photoBase64() != null && !user.photoBase64().isEmpty()) { %>
          <img src="data:image/png;base64,<%= user.photoBase64() %>" alt="<%= user.username() %>" class="image-small" />
        <% } else { %>
          <img src="<%= ctx %>/images/default-avatar.png" alt="default" class="image-small" />
        <% } %>
      </td>
      <td><%= user.username() %></td>
      <td>
        <% if (user.nationality() != null && !user.nationality().isEmpty()) { %>
        <img src="https://flagcdn.com/24x18/<%= user.nationality().toLowerCase() %>.png" alt="<%= user.nationality() %>" title="<%= user.nationality() %>" style="vertical-align:middle; margin-right:4px;" />
        <% } %>
        <%= user.nationality() %>
      </td>
      <td><%= user.wins() %></td>
      <td><%= (user.wins() > 0 ? (user.timePlayed() / user.wins()) + "s" : "-") %></td>
    </tr>
    <%   }
      } else { %>
    <tr><td colspan="6">Sem dados de ranking disponíveis.</td></tr>
    <% } %>
    </tbody>
  </table>
  <a href="pages/dashboard.jsp" class="button is-link is-light mt-4 is-fullwidth">← Back to Dashboard</a>
</section>
</body>
</html>
