package org.rafael.gobangjsp.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.rafael.gobangjsp.validation.FormValidator;
import org.rafael.gobangjsp.util.GameServerClient;
import org.rafael.gobangjsp.common.XmlMessageBuilder;
import org.rafael.gobangjsp.common.ServerResponseHandler;
import org.rafael.gobangjsp.database.UserDatabase;
import org.rafael.gobangjsp.common.UserProfileData;

import java.io.IOException;

@WebServlet(name = "EditServlet", value = "/edit")
@MultipartConfig
public class EditProfileServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String nickname = request.getParameter("nickname");
        String password = request.getParameter("password");
        String nationality = request.getParameter("nationality");
        String ageStr = request.getParameter("age");
        String bgcolor = request.getParameter("bgcolor");
        String photoBase64 = getPhotoBase64(request);

        System.out.println("[EditProfileServlet] Pedido de edicao de perfil para: " + nickname);

        if (!FormValidator.validateRequiredFields(nickname)) {
            System.out.println("[EditProfileServlet] Falha: nickname em falta.");
            forwardWithError(request, response, "O nickname é obrigatório.");
            return;
        }
        Integer age = null;
        if (ageStr != null && !ageStr.trim().isEmpty()) {
            try {
                age = Integer.parseInt(ageStr);
                if (FormValidator.parseValidAge(age) == null) {
                    System.out.println("[EditProfileServlet] Falha: idade inválida.");
                    forwardWithError(request, response, "Idade inválida.");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("[EditProfileServlet] Falha: idade não numérica.");
                forwardWithError(request, response, "Idade inválida.");
                return;
            }
        }

        String xmlRequest = XmlMessageBuilder.buildUpdateProfileRequest(nickname, photoBase64);
        String xsdPath = getClass().getClassLoader().getResource("xsd/gameProtocol.xsd").getPath();
        if (!ServerResponseHandler.validate(xmlRequest, xsdPath)) {
            System.out.println("[EditProfileServlet] Falha: XML de pedido invalido.");
            forwardWithError(request, response, "Erro ao validar XML de pedido de atualização de perfil.");
            return;
        }

        GameServerClient client = new GameServerClient();
        String xmlResponse;
        try {
            xmlResponse = client.sendCommand(xmlRequest);
            System.out.println("[EditProfileServlet] Resposta XML do servidor: " + xmlResponse);
        } catch (Exception e) {
            System.err.println("[EditProfileServlet] Erro na comunicação com o servidor: " + e.getMessage());
            forwardWithError(request, response, "Falha na comunicação com o servidor de jogos.");
            return;
        }

        if (!ServerResponseHandler.validate(xmlResponse, xsdPath)) {
            System.out.println("[EditProfileServlet] Falha: XML de resposta inválido.");
            forwardWithError(request, response, "Resposta inválida do servidor de jogo.");
            return;
        }

        if (ServerResponseHandler.isSuccess(xmlResponse, "updateProfile")) {
            System.out.println("[EditProfileServlet] Perfil atualizado com sucesso para utilizador: " + nickname);
            UserDatabase db = new UserDatabase();
            db.setBackgroundColor(nickname, bgcolor);

            UserProfileData profile = ServerResponseHandler.extractUserProfile(xmlResponse, xsdPath);
            request.getSession().setAttribute("userProfile", profile);
            request.getSession().setAttribute("success", "Perfil atualizado com sucesso!");

            response.sendRedirect(request.getContextPath() + "/pages/dashboard.jsp");
        } else {
            String errorMsg = ServerResponseHandler.getErrorMessage(xmlResponse, "updateProfile");
            System.err.println("[EditProfileServlet] Erro ao atualizar perfil: " + (errorMsg != null ? errorMsg : "Desconhecido"));
            forwardWithError(request, response, errorMsg != null ? errorMsg : "Erro ao atualizar perfil.");
        }
    }

    private void forwardWithError(HttpServletRequest request, HttpServletResponse response, String errorMsg) throws ServletException, IOException {
        request.getSession().setAttribute("error", errorMsg);
        request.getServletContext().getRequestDispatcher("/pages/profile.jsp").forward(request, response);
    }

    private String getPhotoBase64(HttpServletRequest request) {
        try {
            Part photoPart = request.getPart("photo");
            if (photoPart != null && photoPart.getSize() > 0) {
                byte[] photoBytes = photoPart.getInputStream().readAllBytes();
                return java.util.Base64.getEncoder().encodeToString(photoBytes);
            }
        } catch (Exception e) {
            // Ignorar se não houver foto
        }
        return "";
    }
}
