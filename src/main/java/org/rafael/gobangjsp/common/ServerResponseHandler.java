package org.rafael.gobangjsp.common;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.rafael.gobangjsp.common.records.UserProfileData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.StringReader;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import org.rafael.gobangjsp.common.records.ResponseStatus;

/**
 * Classe utilitária para validação e parsing de respostas XML do servidor de jogo.
 * Permite validar o XML com XSD, extrair campos principais e interpretar o resultado.
 * Usa XmlMessageReader para validação XSD.
 */
public class ServerResponseHandler {

    /**
     * Valida o XML recebido com o XSD fornecido.
     * @param xml XML a validar
     * @param xsdPath Caminho para o XSD
     * @return true se válido, false caso contrário
     */
    public static boolean validate(String xml, String xsdPath) {
        try {
            SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
            Schema schema = factory.newSchema(new File(xsdPath));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new StringReader(xml)));
            return true;
        } catch (Exception e) {
            System.err.println("[ServerResponseHandler] Erro de validação XML: " + e.getMessage());
            return false;
        }
    }

    /**
     * Extrai o valor de um campo do elemento <response>.
     * @param xml XML de resposta
     * @param tag Nome do campo a extrair
     * @return Valor do campo, ou null se não existir
     */
    public static String getField(String xml, String tag) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new java.io.ByteArrayInputStream(xml.getBytes()));
            Element response = (Element) doc.getElementsByTagName("response").item(0);
            if (response != null && response.getElementsByTagName(tag).getLength() > 0) {
                return response.getElementsByTagName(tag).item(0).getTextContent();
            }
        } catch (Exception e) {
            System.err.println("[ServerResponseHandler] Erro ao extrair campo '" + tag + "': " + e.getMessage());
        }
        return null;
    }

    /**
     * Verifica se a resposta é de sucesso para a operação esperada.
     * @param xml XML de resposta
     * @param expectedOperation Operação esperada (ex: "register")
     * @return true se status for "success" e operation corresponder
     */
    public static boolean isSuccess(String xml, String expectedOperation) {
        String status = getField(xml, "status");
        String operation = getField(xml, "operation");
        return "success".equalsIgnoreCase(status) && expectedOperation.equalsIgnoreCase(operation);
    }

    /**
     * Extrai a mensagem de erro se a resposta for de erro para a operação esperada.
     * @param xml XML de resposta
     * @param expectedOperation Operação esperada (ex: "register")
     * @return Mensagem de erro, ou null se não for erro ou operação não corresponder
     */
    public static String getErrorMessage(String xml, String expectedOperation) {
        String status = getField(xml, "status");
        String operation = getField(xml, "operation");
        if ("error".equalsIgnoreCase(status) && expectedOperation.equalsIgnoreCase(operation)) {
            return getField(xml, "message");
        }
        return null;
    }

    /**
     * Extrai dados do utilizador do XML de resposta do servidor e cria UserProfileData.
     * Valida o XML com o XSD antes de extrair os dados.
     * @param xml XML de resposta
     * @param xsdPath Caminho para o XSD de validação
     * @return Objeto UserProfileData com os dados do utilizador, ou null em caso de erro
     */
    public static UserProfileData extractUserProfile(String xml, String xsdPath) {
        try {
            if (!validate(xml, xsdPath)) {
                System.err.println("[ServerResponseHandler] XML inválido para UserProfileData.");
                return null;
            }
            Document doc = parseXml(xml);
            if (doc == null) return null;
            Element root = doc.getDocumentElement();

            Element responseElem = root.getTagName().equals("response") ? root :
                (Element) root.getElementsByTagName("response").item(0);
            if (responseElem == null) return null;
            return parseUserProfileFromElement(responseElem);
        } catch (Exception e) {
            System.err.println("[ServerResponseHandler] Erro ao extrair UserProfileData: " + e.getMessage());
            return null;
        }
    }

    /**
     * Extrai dados de ranking do XML de resposta do servidor.
     * Valida o XML com o XSD antes de extrair os dados.
     * @param xml XML de resposta
     * @param xsdPath Caminho para o XSD de validação
     * @return Lista de objetos UserProfileData com os dados de ranking, ou null em caso de erro
     */
    public static List<UserProfileData> extractRanking(String xml, String xsdPath) {
        List<UserProfileData> ranking = new ArrayList<>();
        try {
            if (!validate(xml, xsdPath)) {
                System.err.println("[ServerResponseHandler] XML de ranking inválido.");
                return ranking;
            }
            Document doc = parseXml(xml);
            if (doc == null) return ranking;
            Element root = doc.getDocumentElement();
            org.w3c.dom.NodeList responses = root.getElementsByTagName("response");
            for (int i = 0; i < responses.getLength(); i++) {
                Element resp = (Element) responses.item(i);
                ranking.add(parseUserProfileFromElement(resp));
            }
            ranking.sort((a, b) -> Integer.compare(b.wins(), a.wins()));
        } catch (Exception e) {
            System.err.println("[ServerResponseHandler] Erro ao extrair ranking: " + e.getMessage());
        }
        return ranking;
    }

    /**
     * Lê os campos principais (<status>, <operation>, <message>) de uma resposta XML <response>.
     * @param xml XML de resposta
     * @return ResponseStatus com os campos extraídos
     */
    public static ResponseStatus parseResponseStatus(String xml) {
        try {
            Document doc = XmlMessageReader.parseXml(xml);
            Element root = doc.getDocumentElement();
            String status = XmlMessageReader.getTextValue(root, "status");
            String operation = XmlMessageReader.getTextValue(root, "operation");
            String message = XmlMessageReader.getTextValue(root, "message");
            return new ResponseStatus(status, operation, message);
        } catch (Exception e) {
            System.err.println("[ServerResponseHandler] Erro ao ler resposta: " + e.getMessage());
            return null;
        }
    }

    private static Document parseXml(String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
        } catch (Exception e) {
            System.err.println("[ServerResponseHandler] Erro ao fazer parse do XML: " + e.getMessage());
            return null;
        }
    }

    private static UserProfileData parseUserProfileFromElement(Element elem) {
        String username = getTagValue(elem, "username");
        String nationality = getTagValue(elem, "nationality");
        String ageStr = getTagValue(elem, "age");
        String winsStr = getTagValue(elem, "wins");
        String lossesStr = getTagValue(elem, "losses");
        String timePlayedStr = getTagValue(elem, "timePlayed");
        String photoBase64 = getTagValue(elem, "photo");
        int age = parseIntOrDefault(ageStr, 0);
        int wins = parseIntOrDefault(winsStr, 0);
        int losses = parseIntOrDefault(lossesStr, 0);
        long timePlayed = parseLongOrDefault(timePlayedStr, 0L);
        return new UserProfileData(
            username != null ? username : "",
            age,
            nationality != null ? nationality : "",
            wins,
            losses,
            timePlayed,
            photoBase64 != null ? photoBase64 : ""
        );
    }

    private static int parseIntOrDefault(String value, int def) {
        try { return value != null && !value.isEmpty() ? Integer.parseInt(value) : def; } catch (Exception e) { return def; }
    }
    private static long parseLongOrDefault(String value, long def) {
        try { return value != null && !value.isEmpty() ? Long.parseLong(value) : def; } catch (Exception e) { return def; }
    }

    private static String getTagValue(Element parent, String tag) {
        if (parent.getElementsByTagName(tag).getLength() > 0) {
            return parent.getElementsByTagName(tag).item(0).getTextContent();
        }
        return null;
    }
}
