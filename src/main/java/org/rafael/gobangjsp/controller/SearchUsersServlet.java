package org.rafael.gobangjsp.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.rafael.gobangjsp.util.GameServerClient;
import org.rafael.gobangjsp.common.ServerResponseHandler;

import java.io.IOException;

@WebServlet(name = "SearchUsersServlet", value = "/search-users")
public class SearchUsersServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        System.out.println("[SearchUsersServlet] Pesquisa de utilizadores: '" + username + "'");
        if (username == null || username.trim().isEmpty()) {
            forwardWithError(request, response, "Username em falta.");
            return;
        }
        String xsdPath = getClass().getClassLoader().getResource("xsd/gameProtocol.xsd").getPath();
        String xmlRequest = "<searchUsersRequest><username>" + username + "</username></searchUsersRequest>";
        if (!ServerResponseHandler.validate(xmlRequest, xsdPath)) {
            System.out.println("[SearchUsersServlet] Falha: XML de pedido inválido.");
            forwardWithError(request, response, "Pedido de pesquisa inválido (não cumpre o XSD).");
            return;
        }
        GameServerClient client = new GameServerClient();
        String xmlResponse;
        try {
            xmlResponse = client.sendCommand(xmlRequest);
            System.out.println("[SearchUsersServlet] Resposta XML do servidor: " + xmlResponse);
        } catch (Exception e) {
            System.err.println("[SearchUsersServlet] Erro na comunicação com o servidor: " + e.getMessage());
            forwardWithError(request, response, "Falha na comunicação com o servidor de jogos.");
            return;
        }
        if (!ServerResponseHandler.validate(xmlResponse, xsdPath)) {
            System.out.println("[SearchUsersServlet] Falha: XML de resposta inválido.");
            forwardWithError(request, response, "Resposta inválida do servidor de jogo.");
            return;
        }
        // TODO: Parse XML e construir lista de utilizadores (UserSearchResult)
        // request.setAttribute("users", users);
        request.getRequestDispatcher("/pages/searchUsers.jsp").forward(request, response);
    }

    private void forwardWithError(HttpServletRequest request, HttpServletResponse response, String errorMsg) throws ServletException, IOException {
        request.setAttribute("error", errorMsg);
        request.getRequestDispatcher("/pages/searchUsers.jsp").forward(request, response);
    }

}
