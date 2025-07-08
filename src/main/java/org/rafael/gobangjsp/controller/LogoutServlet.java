package org.rafael.gobangjsp.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.rafael.gobangjsp.common.XmlMessageBuilder;
import org.rafael.gobangjsp.common.ServerResponseHandler;
import org.rafael.gobangjsp.util.GameServerClient;
import java.io.IOException;

@WebServlet(name = "LogoutServlet", value = "/logout")
public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String username = (String) request.getSession().getAttribute("username");
        System.out.println("[LogoutServlet] Logout pedido para utilizador: " + username);
        if (username != null) {
            String xmlRequest = XmlMessageBuilder.buildLogoutRequest(username);
            String xsdPath = getClass().getClassLoader().getResource("xsd/gameProtocol.xsd").getPath();
            GameServerClient client = new GameServerClient();
            try {
                String xmlResponse = client.sendCommand(xmlRequest);
                ServerResponseHandler.validate(xmlResponse, xsdPath);
            } catch (Exception e) {
                System.err.println("[LogoutServlet] Erro ao comunicar logout ao servidor: " + e.getMessage());
                forwardWithError(request, response, "Erro ao comunicar logout ao servidor: " + e.getMessage());
                return;
            }
        }
        request.getSession().invalidate();
        response.sendRedirect(request.getContextPath() + "/pages/login.jsp");
    }

    private void forwardWithError(HttpServletRequest request, HttpServletResponse response, String errorMsg) throws ServletException, IOException {
        request.getSession().setAttribute("error", errorMsg);
        request.getServletContext().getRequestDispatcher("/pages/login.jsp").forward(request, response);
    }
}
