package org.rafael.gobangjsp.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.rafael.gobangjsp.common.ServerResponseHandler;
import org.rafael.gobangjsp.common.XmlMessageBuilder;
import org.rafael.gobangjsp.util.GameServerClient;
import org.rafael.gobangjsp.validation.FormValidator;

import java.io.IOException;

@WebServlet(name = "LoginServlet", value = "/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String nickname = request.getParameter("nickname");
        String password = request.getParameter("password");

        // Validação dos campos obrigatórios
        if (!FormValidator.validateRequiredFields(nickname, password)) {
            request.getSession().setAttribute("errorMsg", "Todos os campos são obrigatórios.");
            request.getServletContext().getRequestDispatcher("/pages/login.jsp").forward(request, response);
            return;
        }

        String loginXml = XmlMessageBuilder.buildLoginRequest(nickname, password);
        String xsdPath = getClass().getClassLoader().getResource("xsd/gameProtocol.xsd").getPath();

        if (!ServerResponseHandler.validate(loginXml, xsdPath)) {
            request.getSession().setAttribute("errorMsg", "XML de login inválido (não cumpre o XSD).");
            request.getServletContext().getRequestDispatcher("/pages/login.jsp").forward(request, response);
            return;
        }

        System.out.println("[LoginServlet] Enviar XML de login para o servidor: " + loginXml);
        GameServerClient client = new GameServerClient();
        String serverResponseXml;
        try {
            serverResponseXml = client.sendCommand(loginXml);
            System.out.println("[LoginServlet] Resposta XML do servidor: " + serverResponseXml);
        } catch (Exception e) {
            System.err.println("[LoginServlet] Erro na comunicação com o servidor: " + e.getMessage());
            request.getSession().setAttribute("errorMsg", "Falha na comunicação com o servidor de jogos.");
            request.getServletContext().getRequestDispatcher("/pages/login.jsp").forward(request, response);
            return;
        }

        if (!ServerResponseHandler.validate(serverResponseXml, xsdPath)) {
            System.err.println("[LoginServlet] XML de resposta inválido (não cumpre o XSD).");
            request.getSession().setAttribute("errorMsg", "Resposta do servidor inválida (XML não cumpre o XSD).");
            request.getServletContext().getRequestDispatcher("/pages/login.jsp").forward(request, response);
            return;
        }

        if (ServerResponseHandler.isSuccess(serverResponseXml, "login")) {
            System.out.println("[LoginServlet] Login efetuado com sucesso para utilizador: " + nickname);
            request.getSession().setAttribute("nickname", nickname);

            response.sendRedirect(request.getContextPath() + "/pages/dashboard.jsp");

        } else {
            String reason = ServerResponseHandler.getErrorMessage(serverResponseXml, "login");
            System.err.println("[LoginServlet] Erro no login: " + (reason != null ? reason : "Desconhecido"));
            request.getSession().setAttribute("errorMsg", "Erro no login: " + (reason != null ? reason : "Desconhecido"));
            request.getServletContext().getRequestDispatcher("/pages/login.jsp").forward(request, response);
        }
    }
}
