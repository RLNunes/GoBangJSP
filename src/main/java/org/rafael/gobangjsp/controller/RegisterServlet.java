package org.rafael.gobangjsp.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import org.rafael.gobangjsp.database.UserDatabase;

@WebServlet(name = "registerServlet", value = "/register")
public class RegisterServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Obter dados do formulário
        String nickname = request.getParameter("nickname");
        String password = request.getParameter("password");
        String nacionalidade = request.getParameter("nationality");
        String idadeStr = request.getParameter("age");
        String fotoBase64 = ""; // Por agora, sem foto

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        // Validação simples
        if (nickname == null || nickname.isEmpty() || password == null || password.isEmpty() || nacionalidade == null || nacionalidade.isEmpty() || idadeStr == null || idadeStr.isEmpty()) {
            out.println("<html><body>");
            out.println("<h2>Erro: Todos os campos são obrigatórios.</h2>");
            out.println("<a href='pages/register.html'>Voltar</a>");
            out.println("</body></html>");
            return;
        }

        int idade;
        try {
            idade = Integer.parseInt(idadeStr);
        } catch (NumberFormatException e) {
            out.println("<html><body>");
            out.println("<h2>Erro: Idade inválida.</h2>");
            out.println("<a href='pages/register.html'>Voltar</a>");
            out.println("</body></html>");
            return;
        }

        // Instanciar ou obter a base de dados de utilizadores
        UserDatabase db = (UserDatabase) getServletContext().getAttribute("userDB");
        if (db == null) {
            db = new UserDatabase();
            getServletContext().setAttribute("userDB", db);
        }

        boolean registado = db.register(nickname, password, idade, nacionalidade, fotoBase64);
        if (registado) {
            out.println("<html><body>");
            out.println("<h2>Registo efetuado com sucesso!</h2>");
            out.println("<a href='login.html'>Ir para Login</a>");
            out.println("</body></html>");
        } else {
            out.println("<html><body>");
            out.println("<h2>Erro: Nickname já existe.</h2>");
            out.println("<a href='pages/register.html'>Voltar</a>");
            out.println("</body></html>");
        }
    }
}
