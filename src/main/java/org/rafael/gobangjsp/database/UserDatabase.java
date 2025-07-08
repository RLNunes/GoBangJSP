package org.rafael.gobangjsp.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Guarda apenas a cor de fundo preferida de cada utilizador (nickname -> backgroundColor).
 * Não gere dados de autenticação, perfil ou estatísticas.
 */
public class UserDatabase implements Serializable {
    private static String getDataDir() {
        String env = System.getenv("GOBANGJSP_DATA_DIR");
        if (env == null || env.isBlank()) {
            throw new IllegalStateException("A variável de ambiente GOBANGJSP_DATA_DIR não está definida. Por favor, consulta as instruções do projeto.");
        }
        return env;
    }

    private static String getDataFilePath(String filename) {
        String dir = getDataDir();
        File parent = new File(dir);
        if (!parent.exists()) {
            boolean created = parent.mkdirs();
            if (created) {
                System.out.println("[UserDatabase] Pasta criada: " + parent.getAbsolutePath());
            } else if (!parent.exists()) {
                System.err.println("[UserDatabase] ERRO: Não foi possível criar a pasta: " + parent.getAbsolutePath());
            }
        }
        return dir + File.separator + filename;
    }

    private ConcurrentHashMap<String, String> backgroundColors;

    public UserDatabase() {
        backgroundColors = loadFromFile();
        if (backgroundColors == null) {
            backgroundColors = new ConcurrentHashMap<>();
        }
    }

    public synchronized void setBackgroundColor(String nickname, String color) {
        if (nickname != null && color != null) {
            backgroundColors.put(nickname, color);
            saveToFile();
        }
    }

    public synchronized String getBackgroundColor(String nickname) {
        return backgroundColors.get(nickname);
    }

    private void saveToFile() {
        try {
            String filePath = getDataFilePath("backgroundColors.ser");
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath));
            oos.writeObject(backgroundColors);
            oos.close();
            System.out.println("[UserDatabase] backgroundColors.ser guardado em: " + filePath);
            exportToXml();
        } catch (IOException e) {
            System.err.println("Erro ao guardar backgroundColors: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private ConcurrentHashMap<String, String> loadFromFile() {
        try {
            new File(getDataDir()).mkdirs();
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(getDataFilePath("backgroundColors.ser")));
            ConcurrentHashMap<String, String> map = (ConcurrentHashMap<String, String>) ois.readObject();
            ois.close();
            return map;
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * Exporta o mapa backgroundColors para um ficheiro XML (backgroundColors.xml).
     * O formato é simples: <backgroundColors><user nickname="..." color="..."/>...</backgroundColors>
     */
    private void exportToXml() {
        String xmlPath = getDataFilePath("backgroundColors.xml");
        try (PrintWriter writer = new PrintWriter(new FileWriter(xmlPath))) {
            writer.println("<backgroundColors>");
            for (var entry : backgroundColors.entrySet()) {
                writer.printf("  <user nickname=\"%s\" color=\"%s\"/>%n", escapeXml(entry.getKey()), escapeXml(entry.getValue()));
            }
            writer.println("</backgroundColors>");
            System.out.println("[UserDatabase] backgroundColors.xml exportado em: " + xmlPath);
        } catch (IOException e) {
            System.err.println("Erro ao exportar backgroundColors para XML: " + e.getMessage());
        }
    }

    // Utilitário simples para escapar caracteres XML
    private String escapeXml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
