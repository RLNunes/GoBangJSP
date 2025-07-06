<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <title>Register</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bulma@0.9.4/css/bulma.min.css" />
  <%
    String ctx = request.getContextPath();
  %>
  <link rel="stylesheet" href="<%= ctx %>/css/style.css" />
</head>
<body>
<div class="custom-box">
  <h2 class="title is-4 has-text-weight-bold mb-5">
    Register New Player
  </h2>
  <c:if test="${not empty errorMsg}">
    <div style="color:red; margin-bottom:1em;">${errorMsg}</div>
  </c:if>
  <form action="${pageContext.request.contextPath}/register" method="post" id="registerForm">
    <div class="field mb-3">
      <label class="label has-text-left" for="nickname">Nickname</label>
        <input
          class="input"
          type="text"
          id="nickname"
          name="nickname"
          placeholder="Enter your nickname"
          value="${param.nickname}"
          required
        />
    </div>
    <div class="field mb-3">
      <label class="label has-text-left" for="password">Password</label>
        <input
          class="input"
          type="password"
          id="password"
          name="password"
          placeholder="Enter your password"
          required
        />
    </div>
    <div class="field mb-3">
      <label class="label has-text-left" for="nationality">Nationality</label>
        <input
          class="input"
          type="text"
          id="nationality"
          name="nationality"
          placeholder="Enter your nationality"
          value="${param.nationality}"
          required
        />
    </div>
    <div class="field mb-3">
      <label class="label has-text-left" for="age">Age</label>
        <input
          class="input"
          type="number"
          id="age"
          name="age"
          min="6"
          placeholder="Enter your age"
          value="${param.age}"
          required
        />
    </div>
    <div class="field mb-3">
      <label class="label has-text-left" for="photo">Profile Picture</label>
        <input
          class="input"
          type="file"
          id="photo"
          name="photo"
          accept="image/*"
        />
    </div>
    <div class="field mb-5">
      <label class="label has-text-left" for="backgroundColor">Preferred Background Color</label>
        <input
          class="input"
          type="color"
          id="backgroundColor"
          name="backgroundColor"
          value="${param.backgroundColor != null ? param.backgroundColor : '#ffffff'}"
        />
    </div>
    <button type="submit" class="btn btn-primary" id="registerBtn" disabled>Register</button>
  </form>
  <a href="../index.jsp" class="button is-link is-light mt-4 is-fullwidth">
    ← Back Home
  </a>
</div>
<script src="../js/register.js"></script>
</body>
</html>
