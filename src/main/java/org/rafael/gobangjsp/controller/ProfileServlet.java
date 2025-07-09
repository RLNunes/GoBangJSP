package org.rafael.gobangjsp.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.rafael.gobangjsp.common.ServerResponseHandler;
import org.rafael.gobangjsp.common.records.UserProfileData;
import org.rafael.gobangjsp.common.XmlMessageBuilder;
import org.rafael.gobangjsp.util.GameServerClient;

import java.io.IOException;

@WebServlet(name = "ProfileServlet", value = "/profile")
public class ProfileServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = (String) request.getSession().getAttribute("username");
        if (username == null) {
            response.sendRedirect(request.getContextPath() + "/pages/login.jsp");
            return;
        }

        String xmlRequest = XmlMessageBuilder.buildRequestProfile(username);
        String xsdPath = getClass().getClassLoader().getResource("xsd/gameProtocol.xsd").getPath();
        GameServerClient client = new GameServerClient();
        String xmlResponse;
        try {
            xmlResponse = client.sendCommand(xmlRequest);
        } catch (Exception e) {
            request.getSession().setAttribute("error", "Erro ao obter perfil do servidor.");
            response.sendRedirect(request.getContextPath() + "/pages/dashboard.jsp");
            return;
        }
        if (!ServerResponseHandler.validate(xmlResponse, xsdPath)) {
            request.getSession().setAttribute("error", "Resposta inválida do servidor.");
            response.sendRedirect(request.getContextPath() + "/pages/dashboard.jsp");
            return;
        }
        UserProfileData profile = ServerResponseHandler.extractUserProfile(xmlResponse, xsdPath);
        request.getSession().setAttribute("userProfile", profile);
        System.out.println("[ProfileServlet] Perfil carregado com sucesso para utilizador: " + username);
        System.out.println("[ProfileServlet] Dados do perfil: " + profile);
        request.getRequestDispatcher("/pages/profile.jsp").forward(request, response);
    }

    private void forwardWithError(HttpServletRequest request, HttpServletResponse response, String errorMsg) throws ServletException, IOException {
        request.getSession().setAttribute("error", errorMsg);
        request.getServletContext().getRequestDispatcher("/pages/dashboard.jsp").forward(request, response);
    }
}
