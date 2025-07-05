package org.rafael.gobangjsp.validation;

/**
 * Classe utilitária para validação dos dados do formulário de registo web.
 * Apenas validação de inputs web — não faz parsing de XML nem comunica com a base de dados.
 * Todos os métodos são estáticos para facilitar o uso direto no servlet.
 */
public final class RegisterFormValidator {

    // Impede instanciação
    private RegisterFormValidator() {}

    /**
     * Valida se todos os campos obrigatórios estão preenchidos.
     * @param nickname Nome de utilizador
     * @param password Palavra-passe
     * @param nationality Nacionalidade
     * @param ageStr Idade em formato String
     * @return true se todos os campos estiverem preenchidos, false caso contrário
     */
    public static boolean validateRequiredFields(String nickname, String password, String nationality, String ageStr) {
        return nickname != null && !nickname.isEmpty()
            && password != null && !password.isEmpty()
            && nationality != null && !nationality.isEmpty()
            && ageStr != null && !ageStr.isEmpty();
    }

    /**
     * Valida e faz parse do campo idade.
     * @param ageStr Idade em formato String
     * @return Integer com a idade válida, ou null se inválida
     */
    public static Integer parseValidAge(String ageStr) {
        try {
            int age = Integer.parseInt(ageStr);
            if (age >= 6 && age <= 120) { // Critério simples: idade razoável
                return age;
            }
        } catch (NumberFormatException e) {
            // Ignorar, devolve null
        }
        return null;
    }

    /**
     * Valida a força da password.
     * Critérios: mínimo 6 caracteres, pelo menos uma letra e um número.
     * @param password Palavra-passe a validar
     * @return true se a password cumprir os critérios, false caso contrário
     */
    public static boolean validatePasswordStrength(String password) {
        if (password == null || password.length() < 6) return false;
        boolean hasLetter = false;
        boolean hasDigit = false;
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) hasLetter = true;
            if (Character.isDigit(c)) hasDigit = true;
        }
        return hasLetter && hasDigit;
    }

    // Métodos para validações adicionais podem ser facilmente adicionados aqui,
    // por exemplo: validação de nacionalidade, nickname, etc.
}

