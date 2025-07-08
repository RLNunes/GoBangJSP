package org.rafael.gobangjsp.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.rafael.gobangjsp.common.UserProfileData;
import org.rafael.gobangjsp.common.ServerResponseHandler;
import org.rafael.gobangjsp.util.GameServerClient;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "RankingServlet", value = "/ranking")
public class RankingServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Pedir o ranking ao servidor de jogo
            GameServerClient client = new GameServerClient();
            String xsdPath = getClass().getClassLoader().getResource("xsd/gameProtocol.xsd").getPath();
            String xmlRequest = "<message><getRankingRequest/></message>";
            String xmlResponse = client.sendCommand(xmlRequest);

            if (!ServerResponseHandler.validate(xmlResponse, xsdPath)) {
                forwardWithError(request, response, "Resposta inválida do servidor ao pedir o ranking.");
                return;
            }

            // Extrair lista de utilizadores do ranking
            List<UserProfileData> ranking = ServerResponseHandler.extractRanking(xmlResponse, xsdPath);
            request.setAttribute("gameHistory", ranking);
            request.getRequestDispatcher("/pages/ranking.jsp").forward(request, response);

            System.out.println("[RankingServlet] ranking 1 : " + ranking.get(0));

        } catch (Exception e) {
            forwardWithError(request, response, "Erro ao obter ranking: " + e.getMessage());
        }
    }

    private void forwardWithError(HttpServletRequest request, HttpServletResponse response, String errorMsg) throws ServletException, IOException {
        request.getSession().setAttribute("error", errorMsg);
        request.getServletContext().getRequestDispatcher("/pages/dashboard.jsp").forward(request, response);
    }
}
