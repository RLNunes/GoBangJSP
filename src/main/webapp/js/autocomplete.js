// AutoComplete modular para pesquisa de utilizadores em profile.jsp
// Requer: campo #searchUser e div #autocomplete-list

document.addEventListener('DOMContentLoaded', () => {
    const input = document.getElementById('searchUser');
    const suggestionList = document.getElementById('autocomplete-list');
    let debounceTimer = null;

    // Evento ao escrever no campo de pesquisa
    input.addEventListener('input', () => {
        clearTimeout(debounceTimer);
        const query = input.value.trim();

        if (query.length < 2) {
            clearSuggestions();
            return;
        }

        debounceTimer = setTimeout(() => fetchUserSuggestions(query), 250);
    });

    // Faz pedido AJAX ao backend
    function fetchUserSuggestions(query) {
        fetch(`search-users?username=${encodeURIComponent(query)}`)
            .then(r => r.json())
            .then(users => renderSuggestions(users))
            .catch(() => showError('Erro ao pesquisar utilizadores.'));
    }

    // Renderiza as sugestões na lista
    function renderSuggestions(users) {
        clearSuggestions();

        if (!users.length) {
            showError('Nenhum utilizador encontrado.');
            return;
        }

        users.forEach(user => {
            suggestionList.appendChild(createSuggestionItem(user));
        });
    }

    // Cria o HTML de uma sugestão individual
    function createSuggestionItem(user) {
        const div = document.createElement('div');
        div.className = 'autocomplete-item';

        div.innerHTML = `
            <span class="avatar">
                ${getAvatarHTML(user)}
            </span>
            <span class="user-info">
                <strong>${user.username}</strong>
                <small>${user.fullName || ''}</small>
                ${getNationalityHTML(user)}
                ${getStatusHTML(user)}
            </span>
        `;

        div.onclick = () => {
            input.value = user.username;
            clearSuggestions();
        };

        return div;
    }


    function getAvatarHTML(user) {
        return user.photo
            ? `<img src="data:image/png;base64,${user.photo}" alt="avatar" class="avatar-img"/>`
            : `<span class="avatar-placeholder">👤</span>`;
    }

    function getNationalityHTML(user) {
        return user.nationality
            ? `<img src="https://flagcdn.com/16x12/${user.nationality.toLowerCase()}.png" 
                    alt="${user.nationality}" title="${user.nationality}" 
                    style="margin-left:4px;vertical-align:middle;"/>`
            : '';
    }

    function getStatusHTML(user) {
        return `<span class="status-dot ${user.isLoggedIn ? 'online' : 'offline'}"></span>`;
    }

    // Limpa sugestões da lista
    function clearSuggestions() {
        suggestionList.innerHTML = '';
    }

    // Mostra mensagem de erro simples
    function showError(msg) {
        suggestionList.innerHTML = `<div class="autocomplete-item">${msg}</div>`;
    }
});