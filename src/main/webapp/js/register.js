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

   /* form.addEventListener('submit', function (e) {
        e.preventDefault();
        btn.disabled = true;
        errorDiv.textContent = '';
        const formData = new FormData(form);
        fetch(form.action, {
            method: 'POST',
            body: formData
        })
        .then(response => {
            if (response.redirected) {
                window.location.href = response.url;
                return null;
            }
            return response.text();
        })
        .then(text => {
            if (text) {
                // Tenta extrair mensagem de erro do HTML devolvido pelo servlet
                console.log(text)
                const match = text.match(/<h2>Erro: (.*?)<\/h2>/i);
                errorDiv.textContent = match ? match[1] : 'Erro no registo.';
            }
        })
        .catch(() => {
            errorDiv.textContent = 'Erro de comunicação com o servidor.';
        })
        .finally(() => {
            btn.disabled = false;
        });
    });*/
});

