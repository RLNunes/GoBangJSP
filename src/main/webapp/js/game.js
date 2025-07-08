
const BOARD_SIZE = 15;
const TOTAL_CELLS = BOARD_SIZE * BOARD_SIZE;
let boardElement, timerElement, turnElement, restartBtn;
let board = Array(TOTAL_CELLS).fill(0);
let currentPlayer = 1;
let seconds = 30;
let timer;
let gameActive = true;

document.addEventListener('DOMContentLoaded', function() {
    boardElement = document.getElementById("board") || document.getElementById("game-container");
    timerElement = document.getElementById("timer");
    turnElement = document.getElementById("turn");
    restartBtn = document.getElementById("restartBtn");

    if (!boardElement) return;
    if (!document.getElementById("board")) {
        // Cria o tabuleiro se não existir
        let boardDiv = document.createElement("div");
        boardDiv.id = "board";
        boardDiv.setAttribute('role', 'grid');
        boardDiv.setAttribute('aria-label', 'Game board');
        boardElement.appendChild(boardDiv);
        boardElement = boardDiv;
    }
    if (!timerElement) {
        timerElement = document.createElement("div");
        timerElement.id = "timer";
        boardElement.parentNode.insertBefore(timerElement, boardElement);
    }
    if (!turnElement) {
        turnElement = document.createElement("div");
        turnElement.id = "turn";
        boardElement.parentNode.insertBefore(turnElement, timerElement);
    }
    if (!restartBtn) {
        restartBtn = document.createElement("button");
        restartBtn.id = "restartBtn";
        restartBtn.className = "btn btn-primary mt-4";
        restartBtn.textContent = "Restart Game";
        boardElement.parentNode.appendChild(restartBtn);
    }

    for (let i = 0; i < TOTAL_CELLS; i++) {
        let cell = document.createElement("div");
        cell.classList.add("cell");
        cell.dataset.pos = i;
        cell.setAttribute('role', 'button');
        cell.setAttribute('tabindex', 0);
        cell.addEventListener("click", () => makeMove(i));
        boardElement.appendChild(cell);
    }
    restartBtn.addEventListener('click', restartGame);
    updateBoard();
    turnElement.textContent = `Current turn: Player 1 (Green)`;
    restartBtn.style.display = "none";
    resetTimer();
});

function makeMove(pos) {
    if (!gameActive) return;
    if (board[pos] !== 0) {
        alert("This cell is already taken!");
        return;
    }
    board[pos] = currentPlayer;
    updateBoard();
    if (checkWin(pos)) {
        clearInterval(timer);
        gameActive = false;
        restartBtn.style.display = "inline-block";
        setTimeout(() => {
            alert(`Player ${currentPlayer} wins!`);
        }, 5);
        return;
    }
    switchPlayer();
}

function updateBoard() {
    for (let i = 0; i < TOTAL_CELLS; i++) {
        let cell = boardElement.children[i];
        cell.classList.remove("player1", "player2");
        cell.textContent = '';
        if (board[i] === 1) {
            cell.classList.add("player1");
            cell.textContent = "●";
        } else if (board[i] === 2) {
            cell.classList.add("player2");
            cell.textContent = "●";
        }
    }
}

function switchPlayer() {
    currentPlayer = currentPlayer === 1 ? 2 : 1;
    turnElement.textContent = `Current turn: Player ${currentPlayer} (${currentPlayer === 1 ? 'Green' : 'Red'})`;
    resetTimer();
}

function resetTimer() {
    clearInterval(timer);
    seconds = 30;
    timerElement.textContent = `Time left: ${seconds}s`;
    timer = setInterval(() => {
        seconds--;
        timerElement.textContent = `Time left: ${seconds}s`;
        if (seconds <= 0) {
            clearInterval(timer);
            alert(`Player ${currentPlayer} ran out of time! Turn lost.`);
            switchPlayer();
        }
    }, 1000);
}

function checkWin(pos) {
    const directions = [1, BOARD_SIZE, BOARD_SIZE + 1, BOARD_SIZE - 1];
    for (let dir of directions) {
        let count = 1;
        count += countInDirection(pos, dir, currentPlayer);
        count += countInDirection(pos, -dir, currentPlayer);
        if (count >= 5) {
            return true;
        }
    }
    return false;
}

function countInDirection(startPos, step, player) {
    let count = 0;
    let pos = startPos + step;
    while (
        pos >= 0 &&
        pos < TOTAL_CELLS &&
        board[pos] === player &&
        isValidStep(startPos, pos, step)
    ) {
        count++;
        pos += step;
    }
    return count;
}

function isValidStep(startPos, nextPos, step) {
    if (step === 1 || step === -1) {
        return Math.floor(startPos / BOARD_SIZE) === Math.floor(nextPos / BOARD_SIZE);
    }
    return true;
}

function restartGame() {
    board.fill(0);
    updateBoard();
    currentPlayer = 1;
    gameActive = true;
    turnElement.textContent = `Current turn: Player 1 (Green)`;
    restartBtn.style.display = "none";
    resetTimer();
}

