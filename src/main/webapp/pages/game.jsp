<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>GoBang - Game</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bulma@0.9.4/css/bulma.min.css" />
    <% String ctx = request.getContextPath(); %>
    <link rel="stylesheet" href="<%= ctx %>/css/style.css" />
</head>
<body>
<div class="custom-box">
    <h1 class="title">GoBang</h1>
    <div class="field">
        <label class="label has-text-left" for="searchUser">Pesquisar Oponente</label>
        <div class="control has-icons-left">
            <input class="input" type="text" id="searchUser" name="searchUser" placeholder="Pesquisar utilizador..." autocomplete="off" />
            <span class="icon is-left"><i class="fas fa-search"></i></span>
            <div id="autocomplete-list" class="autocomplete-items"></div>
        </div>
    </div>
    <button id="startGameBtn" class="button is-link mt-3">Começar Jogo</button>
    <button id="findMatchBtn" class="button is-warning mt-3">Find Match</button>
    <div id="game-container" style="display:none;">
        <!-- Aqui será renderizado o tabuleiro do jogo via JS -->
    </div>
    <div class="buttons mt-4">
        <a href="<%= ctx %>/dashboard.jsp" class="button is-link is-light">← Back to Dashboard</a>
    </div>
    <script src="<%= ctx %>/js/autocomplete.js"></script>
    <script src="<%= ctx %>/js/game.js"></script>
    <script>
    document.getElementById('startGameBtn').onclick = function() {
        document.getElementById('game-container').style.display = '';
        this.style.display = 'none';
        document.getElementById('searchUser').disabled = true;
        document.getElementById('autocomplete-list').style.display = 'none';
    };

    document.getElementById('findMatchBtn').onclick = function() {
        fetch('<%= ctx %>/find-match', { method: 'POST' })
            .then(r => r.json())
            .then(data => {
                if (data.success) {
                    alert('Matchmaking iniciado! Aguarde por um adversário...');
                } else {
                    alert('Erro ao procurar adversário: ' + (data.error || 'Desconhecido'));
                }
            })
            .catch(() => alert('Erro de comunicação com o servidor de jogo.'));
    };
    </script>
</div>
</body>
</html>
