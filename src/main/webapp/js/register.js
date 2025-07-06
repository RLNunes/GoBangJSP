// Validação simples de campos obrigatórios para ativar/desativar o botão de registo

document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('registerForm');
    const btn = document.getElementById('registerBtn');
    const requiredFields = [
        document.getElementById('nickname'),
        document.getElementById('password'),
        document.getElementById('nationality'),
        document.getElementById('age')
    ];
    const errorDiv = document.createElement('div');
    errorDiv.id = 'registerErrorMsg';
    errorDiv.style.color = 'red';
    errorDiv.style.marginBottom = '1em';
    form.insertBefore(errorDiv, form.firstChild);

    function areRequiredFieldsFilled() {
        return requiredFields.every(input => input.value && input.value.trim() !== '');
    }

    function checkFields() {
        btn.disabled = !areRequiredFieldsFilled();
    }

    requiredFields.forEach(input => {
        input.addEventListener('input', checkFields);
    });

    // Verificação inicial
    btn.disabled = !areRequiredFieldsFilled();
});
