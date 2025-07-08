package org.rafael.gobangjsp.validation;

public final class FormValidator {

    /**
     * Valida se todos os campos obrigatórios estão preenchidos.
     * Recebe um array de campos obrigatórios (String ou Integer convertido para String).
     * Retorna true se todos estiverem preenchidos, false caso contrário.
     */
    public static boolean validateRequiredFields(Object... fields) {
        for (Object field : fields) {
            if (field == null) return false;
            if (field instanceof String && ((String) field).trim().isEmpty()) return false;
        }
        return true;
    }

    /**
     * Valida e faz parse do campo idade.
     *
     * @param age Idade
     * @return Integer com a idade válida, ou null se inválida
     */
    public static Integer parseValidAge(Integer age) {
        if (age >= 6 && age <= 120) { // Critério simples: idade razoável
            return age;
        }
        return null;
    }

    /**
     * Valida a força da password.
     * Critérios: mínimo 6 caracteres, pelo menos uma letra e um número.
     *
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
}
