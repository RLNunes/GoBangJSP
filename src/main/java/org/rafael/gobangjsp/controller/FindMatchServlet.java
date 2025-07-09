package org.rafael.gobangjsp.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.rafael.gobangjsp.common.XmlMessageReader;
import org.rafael.gobangjsp.common.records.ResponseStatus;
import org.rafael.gobangjsp.util.GameServerClient;
import org.rafael.gobangjsp.common.ServerResponseHandler;
import org.rafael.gobangjsp.common.XmlMessageBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.IOException;

@WebServlet(name = "FindMatchServlet", value = "/find-match")
public class FindMatchServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = (String) request.getSession().getAttribute("username");
        System.out.println("[FindMatchServlet] Pedido de matchmaking para utilizador: " + username);
        if (username == null) {
            forwardWithError(request, response, "Utilizador não autenticado.");
            return;
        }
        String xsdPath = getClass().getClassLoader().getResource("xsd/gameProtocol.xsd").getPath();
        String xmlRequest = XmlMessageBuilder.buildFindMatchRequest(username);
        if (!ServerResponseHandler.validate(xmlRequest, xsdPath)) {
            System.out.println("[FindMatchServlet] Falha: XML de pedido inválido.");
            forwardWithError(request, response, "Pedido de matchmaking inválido (não cumpre o XSD).");
            return;
        }
        GameServerClient client = new GameServerClient();
        String xmlResponse;
        try {
            xmlResponse = client.sendCommand(xmlRequest);
            System.out.println("[FindMatchServlet] Resposta XML do servidor: " + xmlResponse);
        } catch (Exception e) {
            System.err.println("[FindMatchServlet] Erro na comunicação com o servidor: " + e.getMessage());
            forwardWithError(request, response, "Falha na comunicação com o servidor de jogos.");
            return;
        }
        if (!ServerResponseHandler.validate(xmlResponse, xsdPath)) {
            System.out.println("[FindMatchServlet] Falha: XML de resposta inválido.");
            forwardWithError(request, response, "Resposta inválida do servidor de jogo.");
            return;
        }
        try {
            ResponseStatus resp = ServerResponseHandler.parseResponseStatus(xmlResponse);
            if (resp != null && "success".equalsIgnoreCase(resp.status()) && "findMatch".equalsIgnoreCase(resp.operation())) {
                System.out.println("[FindMatchServlet] Matchmaking iniciado com sucesso.");

                request.setAttribute("matchmakingStatus", "waiting");
                request.getRequestDispatcher("/pages/game.jsp").forward(request, response);
            } else {
                System.out.println("[FindMatchServlet] Erro no matchmaking: " + (resp != null ? resp.message() : "Resposta inválida."));
                String msg = (resp != null && resp.message() != null) ? resp.message() : "Erro no matchmaking.";
                forwardWithError(request, response, msg);
            }
        } catch (Exception e) {
            System.err.println("[FindMatchServlet] Erro ao processar resposta: " + e.getMessage());
            forwardWithError(request, response, "Erro ao processar resposta do servidor de jogos.");
        }
    }

    private void forwardWithError(HttpServletRequest request, HttpServletResponse response, String errorMsg) throws IOException, ServletException {
        request.getSession().setAttribute("error", errorMsg);
        request.getServletContext().getRequestDispatcher("/pages/game.jsp").forward(request, response);
    }
}
