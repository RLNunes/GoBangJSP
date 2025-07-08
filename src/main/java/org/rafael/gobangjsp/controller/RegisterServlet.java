package org.rafael.gobangjsp.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.util.Base64;
import org.rafael.gobangjsp.common.*;
import org.rafael.gobangjsp.util.GameServerClient;
import org.rafael.gobangjsp.validation.FormValidator;

@WebServlet(name = "registerServlet", value = "/register")
@MultipartConfig
public class RegisterServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String nickname = request.getParameter("nickname");
        String password = request.getParameter("password");
        String nationality = request.getParameter("nationality");
        String ageStr = request.getParameter("age");
        String backgroundColor = request.getParameter("backgroundColor");
        String photoBase64 = getPhotoBase64(request);

        System.out.println("[RegisterServlet] Tentativa de registo para: " + nickname);

        // Validação dos campos obrigatórios
        if (!FormValidator.validateRequiredFields(nickname, password, nationality, ageStr, photoBase64)) {
            System.out.println("[RegisterServlet] Falha: campos obrigatórios em falta.");
            forwardWithError(request, response, "Todos os campos são obrigatórios.");
            return;
        }

        // Validação da força da password
        if (!FormValidator.validatePasswordStrength(password)) {
            System.out.println("[RegisterServlet] Falha: password fraca.");
            forwardWithError(request, response, "A password deve ter pelo menos 6 caracteres, incluindo letras e números.");
            return;
        }

        // Validação e parsing da idade
        Integer age = null;
        try {
            age = Integer.parseInt(ageStr);
        } catch (NumberFormatException e) {
            System.out.println("[RegisterServlet] Falha: idade inválida (não numérica).");
            forwardWithError(request, response, "Idade inválida (deve ser um número).");
            return;
        }
        if (FormValidator.parseValidAge(age) == null) {
            System.out.println("[RegisterServlet] Falha: idade fora do intervalo permitido.");
            forwardWithError(request, response, "Idade inválida (deve ser um número entre 6 e 120).");
            return;
        }

        String registerXml = XmlMessageBuilder.buildRegisterRequest(nickname, password, age, nationality, photoBase64);
        String xsdPath = getClass().getClassLoader().getResource("xsd/gameProtocol.xsd").getPath();
        if (!ServerResponseHandler.validate(registerXml, xsdPath)) {
            System.out.println("[RegisterServlet] Falha: XML de registo inválido.");
            forwardWithError(request, response, "XML de registo inválido (não cumpre o XSD).");
            return;
        }

        GameServerClient client = new GameServerClient();
        String serverResponseXml;
        try {
            serverResponseXml = client.sendCommand(registerXml);
            System.out.println("[RegisterServlet] Resposta XML do servidor: " + serverResponseXml);
        } catch (Exception e) {
            System.err.println("[RegisterServlet] Erro na comunicação com o servidor: " + e.getMessage());
            forwardWithError(request, response, "Falha na comunicação com o servidor de jogos.");
            return;
        }
        if (!ServerResponseHandler.validate(serverResponseXml, xsdPath)) {
            System.out.println("[RegisterServlet] Falha: XML de resposta inválido.");
            forwardWithError(request, response, "Resposta do servidor inválida (XML não cumpre o XSD).");
            return;
        }

        if (ServerResponseHandler.isSuccess(serverResponseXml, "register")) {
            System.out.println("[RegisterServlet] Registo efetuado com sucesso para utilizador: " + nickname);
            // Extrair dados reais do utilizador do XML de resposta do servidor
            UserProfileData profile = ServerResponseHandler.extractUserProfile(serverResponseXml, xsdPath);
            request.getSession().setAttribute("userProfile", profile);
            request.getSession().setAttribute("nickname", nickname);
            response.sendRedirect(request.getContextPath() + "/pages/dashboard.jsp");
        } else {
            String reason = ServerResponseHandler.getErrorMessage(serverResponseXml, "register");
            System.err.println("[RegisterServlet] Erro no registo: " + (reason != null ? reason : "Desconhecido"));
            forwardWithError(request, response, "Erro no registo: " + (reason != null ? reason : "Desconhecido"));
        }
    }

    private void forwardWithError(HttpServletRequest request, HttpServletResponse response, String errorMsg) throws ServletException, IOException {
        request.getSession().setAttribute("error", errorMsg);
        request.getServletContext().getRequestDispatcher("/pages/register.jsp").forward(request, response);
    }

    private String getPhotoBase64(HttpServletRequest request) {
        try {
            Part photoPart = request.getPart("photo");
            if (photoPart != null && photoPart.getSize() > 0) {
                byte[] photoBytes = photoPart.getInputStream().readAllBytes();
                return Base64.getEncoder().encodeToString(photoBytes);
            }
        } catch (Exception e) {
            // Ignore if no photo
        }
        return "";
    }
}
