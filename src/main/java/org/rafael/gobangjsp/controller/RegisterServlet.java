package org.rafael.gobangjsp.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Base64;
import org.rafael.gobangjsp.common.*;
import org.rafael.gobangjsp.util.GameServerClient;
import org.rafael.gobangjsp.validation.RegisterFormValidator;

@WebServlet(name = "registerServlet", value = "/register")
public class RegisterServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String nickname = request.getParameter("nickname");
        String password = request.getParameter("password");
        String nationality = request.getParameter("nationality");
        String ageStr = request.getParameter("age");
        String backgroundColor = request.getParameter("backgroundColor");
        String photoBase64 = getPhotoBase64(request);

        // Validação dos campos obrigatórios
        if (!RegisterFormValidator.validateRequiredFields(nickname, password, nationality, ageStr)) {
            showError(out, "Todos os campos são obrigatórios.");
            return;
        }

        // Validação da força da password (opcional, mas recomendada)
        if (!RegisterFormValidator.validatePasswordStrength(password)) {
            showError(out, "A password deve ter pelo menos 6 caracteres, incluindo letras e números.");
            return;
        }

        // Validação e parsing da idade
        Integer age = RegisterFormValidator.parseValidAge(ageStr);
        if (age == null) {
            showError(out, "Idade inválida (deve ser um número entre 6 e 120).");
            return;
        }

        String registerXml = XmlMessageBuilder.buildRegisterRequest(nickname, password, age, nationality, photoBase64);
        String xsdPath = request.getServletContext().getRealPath("WEB-INF/classes/org/rafael/gobangjsp/common/xsd/gameProtocol.xsd");
        if (!XmlMessageReader.validateXml(registerXml, xsdPath)) {
            showError(out, "XML de registo inválido (não cumpre o XSD).");
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
            showError(out, "Falha na comunicação com o servidor de jogos.");
            return;
        }

        if (!ServerResponseHandler.validate(serverResponseXml, xsdPath)) {
            System.err.println("[RegisterServlet] XML de resposta inválido (não cumpre o XSD).");
            showError(out, "Resposta do servidor inválida (XML não cumpre o XSD).");
            return;
        }

        if (ServerResponseHandler.isSuccess(serverResponseXml, "register")) {
            System.out.println("[RegisterServlet] Registo efetuado com sucesso para utilizador: " + nickname);
            // Save backgroundColor locally if needed
            showSuccess(out);
        } else {
            String reason = ServerResponseHandler.getErrorMessage(serverResponseXml, "register");
            System.err.println("[RegisterServlet] Erro no registo: " + (reason != null ? reason : "Desconhecido"));
            showError(out, "Erro no registo: " + (reason != null ? reason : "Desconhecido"));
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

    private void showError(PrintWriter out, String message) {
        out.println("<html><body>");
        out.println("<h2>Erro: " + message + "</h2>");
        out.println("<a href='pages/register.html'>Voltar</a>");
        out.println("</body></html>");
    }

    private void showSuccess(PrintWriter out) {
        out.println("<html><body>");
        out.println("<h2>Registo efetuado com sucesso!</h2>");
        out.println("<a href='pages/login.html'>Ir para Login</a>");
        out.println("</body></html>");
    }
}
