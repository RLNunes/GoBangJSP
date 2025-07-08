package org.rafael.gobangjsp.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.rafael.gobangjsp.common.ServerResponseHandler;
import org.rafael.gobangjsp.common.UserProfileData;
import org.rafael.gobangjsp.common.XmlMessageBuilder;
import org.rafael.gobangjsp.util.GameServerClient;
import org.rafael.gobangjsp.validation.FormValidator;

import java.io.IOException;

@WebServlet(name = "LoginServlet", value = "/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        System.out.println("[LoginServlet] Tentativa de login para: " + username);

        // Validação dos campos obrigatórios
        if (!FormValidator.validateRequiredFields(username, password)) {
            System.out.println("[LoginServlet] Falha: campos obrigatórios em falta.");
            forwardWithError(request, response, "Todos os campos são obrigatórios.");
            return;
        }

        String loginXml = XmlMessageBuilder.buildLoginRequest(username, password);
        String xsdPath = getClass().getClassLoader().getResource("xsd/gameProtocol.xsd").getPath();

        if (!ServerResponseHandler.validate(loginXml, xsdPath)) {
            System.out.println("[LoginServlet] Falha: XML de login inválido.");
            forwardWithError(request, response, "XML de login inválido (não cumpre o XSD).");
            return;
        }

        GameServerClient client = new GameServerClient();
        String serverResponseXml;
        try {
            serverResponseXml = client.sendCommand(loginXml);
            System.out.println("[LoginServlet] Resposta XML do servidor: " + serverResponseXml);
        } catch (Exception e) {
            System.err.println("[LoginServlet] Erro na comunicação com o servidor: " + e.getMessage());
            forwardWithError(request, response, "Falha na comunicação com o servidor de jogos.");
            return;
        }

        if (!ServerResponseHandler.validate(serverResponseXml, xsdPath)) {
            System.out.println("[LoginServlet] Falha: XML de resposta inválido.");
            forwardWithError(request, response, "Resposta do servidor inválida (XML não cumpre o XSD).");
            return;
        }

        if (ServerResponseHandler.isSuccess(serverResponseXml, "login")) {
            System.out.println("[LoginServlet] Login efetuado com sucesso para utilizador: " + username);
            // Extrair dados reais do utilizador do XML de resposta do servidor
            UserProfileData profile = ServerResponseHandler.extractUserProfile(serverResponseXml, xsdPath);
            request.getSession().setAttribute("userProfile", profile);
            request.getSession().setAttribute("username", username);
            response.sendRedirect(request.getContextPath() + "/pages/dashboard.jsp");
        } else {
            String reason = ServerResponseHandler.getErrorMessage(serverResponseXml, "login");
            System.err.println("[LoginServlet] Erro no login: " + (reason != null ? reason : "Desconhecido"));
            forwardWithError(request, response, "Erro no login: " + (reason != null ? reason : "Desconhecido"));
        }
    }

    private void forwardWithError(HttpServletRequest request, HttpServletResponse response, String errorMsg) throws ServletException, IOException {
        request.getSession().setAttribute("error", errorMsg);
        request.getServletContext().getRequestDispatcher("/pages/login.jsp").forward(request, response);
    }
}
