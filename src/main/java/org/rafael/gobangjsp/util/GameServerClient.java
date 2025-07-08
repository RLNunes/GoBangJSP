package org.rafael.gobangjsp.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Classe utilitária para comunicação TCP/IP com um servidor de jogos remoto.
 * Permite enviar comandos e receber respostas como String.
 * Uso típico:
 *   GameServerClient client = new GameServerClient("127.0.0.1", 12345);
 *   String resposta = client.sendCommand("LIST_GAMES");
 */
public class GameServerClient {
    private final String serverIp;
    private final int serverPort;

    private static final String DEFAULT_SERVER_IP = "127.0.0.1";
    private static final int DEFAULT_SERVER_PORT = 8082;

    /**
     * Construtor sem argumentos: usa IP e porto predefinidos.
     */
    public GameServerClient() {
        this(DEFAULT_SERVER_IP, DEFAULT_SERVER_PORT);
    }

    public GameServerClient(String serverIp, int serverPort) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
    }

    /**
     * Envia um comando ao servidor e devolve a resposta como String.
     * @param command Comando a enviar
     * @return Resposta do servidor
     * @throws IOException Em caso de erro de comunicação
     */
    public String sendCommand(String command) throws IOException {
        try (Socket socket = new Socket(serverIp, serverPort);
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out.println(command);

            return in.readLine();
        }
    }
}
