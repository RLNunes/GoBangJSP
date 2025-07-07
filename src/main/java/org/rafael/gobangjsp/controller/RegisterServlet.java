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
        // Não definir contentType aqui, pois o forward trata disso

        String nickname = request.getParameter("nickname");
        String password = request.getParameter("password");
        String nationality = request.getParameter("nationality");
        String ageStr = request.getParameter("age");
        String backgroundColor = request.getParameter("backgroundColor");
        String photoBase64 = getPhotoBase64(request);



        // Validação dos campos obrigatórios
        if (!FormValidator.validateRequiredFields(nickname, password, nationality, Integer.parseInt(ageStr), photoBase64)) {
            request.getSession().setAttribute("errorMsg", "Todos os campos são obrigatórios.");
            request.getServletContext().getRequestDispatcher("/pages/register.jsp").forward(request, response);
            return;
        }

        // Validação da força da password
        if (!FormValidator.validatePasswordStrength(password)) {
            request.getSession().setAttribute("errorMsg", "A password deve ter pelo menos 6 caracteres, incluindo letras e números.");
            request.getServletContext().getRequestDispatcher("/pages/register.jsp").forward(request, response);
            return;
        }

        // Validação e parsing da idade
        Integer age = FormValidator.parseValidAge(Integer.parseInt(ageStr));
        if (age == null) {
            request.getSession().setAttribute("errorMsg", "Idade inválida (deve ser um número entre 6 e 120).");
            request.getServletContext().getRequestDispatcher("/pages/register.jsp").forward(request, response);
            return;
        }

        String registerXml = XmlMessageBuilder.buildRegisterRequest(nickname, password, age, nationality, photoBase64);

        String xsdPath = getClass().getClassLoader().getResource("xsd/gameProtocol.xsd").getPath();

        if (!ServerResponseHandler.validate(registerXml, xsdPath)) {
            request.getSession().setAttribute("errorMsg", "XML de registo inválido (não cumpre o XSD).");
            request.getServletContext().getRequestDispatcher("/pages/register.jsp").forward(request, response);
            return;
        }

        System.out.println("[RegisterServlet] Enviar XML de registo para o servidor: " + registerXml);
        GameServerClient client = new GameServerClient();
        String serverResponseXml;
        try {
            serverResponseXml = client.sendCommand(registerXml);
            System.out.println("[RegisterServlet] Resposta XML do servidor: " + serverResponseXml);
        } catch (Exception e) {
            System.err.println("[RegisterServlet] Erro na comunicação com o servidor: " + e.getMessage());
            request.getSession().setAttribute("errorMsg", "Falha na comunicação com o servidor de jogos.");
            request.getServletContext().getRequestDispatcher("/pages/register.jsp").forward(request, response);
            return;
        }

        if (!ServerResponseHandler.validate(serverResponseXml, xsdPath)) {
            System.err.println("[RegisterServlet] XML de resposta inválido (não cumpre o XSD).");
            request.getSession().setAttribute("errorMsg", "Resposta do servidor inválida (XML não cumpre o XSD).");
            request.getServletContext().getRequestDispatcher("/pages/register.jsp").forward(request, response);
            return;
        }

        if (ServerResponseHandler.isSuccess(serverResponseXml, "register")) {
            System.out.println("[RegisterServlet] Registo efetuado com sucesso para utilizador: " + nickname);
            request.getSession().setAttribute("nickname", nickname);

            response.sendRedirect(request.getContextPath() + "/pages/dashboard.jsp");

        } else {
            String reason = ServerResponseHandler.getErrorMessage(serverResponseXml, "register");
            System.err.println("[RegisterServlet] Erro no registo: " + (reason != null ? reason : "Desconhecido"));
            request.getSession().setAttribute("errorMsg", "Erro no registo: " + (reason != null ? reason : "Desconhecido"));
            request.getServletContext().getRequestDispatcher("/pages/register.jsp").forward(request, response);
        }
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
