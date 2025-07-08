package org.rafael.gobangjsp.common;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.StringReader;
import java.io.ByteArrayInputStream;

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
            System.err.println("Erro de validação XML: " + e.getMessage());
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
            // Ignorar, devolve null
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
            // Validar o XML antes de processar
            if (!validate(xml, xsdPath)) {
                System.err.println("[ServerResponseHandler] XML inválido para UserProfileData.");
                return null;
            }
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            Document doc = factory.newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
            Element root = doc.getDocumentElement();
            // Extrair diretamente os campos do <response>
            String username = getTagValue(root, "username");
            String nationality = getTagValue(root, "nationality");
            String ageStr = getTagValue(root, "age");
            String winsStr = getTagValue(root, "wins");
            String lossesStr = getTagValue(root, "losses");
            String timePlayedStr = getTagValue(root, "timePlayed");
            String photoBase64 = getTagValue(root, "photo");
            int age = ageStr != null && !ageStr.isEmpty() ? Integer.parseInt(ageStr) : 0;
            int wins = winsStr != null && !winsStr.isEmpty() ? Integer.parseInt(winsStr) : 0;
            int losses = lossesStr != null && !lossesStr.isEmpty() ? Integer.parseInt(lossesStr) : 0;
            long timePlayed = timePlayedStr != null && !timePlayedStr.isEmpty() ? Long.parseLong(timePlayedStr) : 0L;
            return new UserProfileData(
                username != null ? username : "",
                age,
                nationality != null ? nationality : "",
                wins,
                losses,
                timePlayed,
                photoBase64 != null ? photoBase64 : ""
            );
        } catch (Exception e) {
            System.err.println("[ServerResponseHandler] Erro ao extrair UserProfileData: " + e.getMessage());
            return null;
        }
    }

    // Método utilitário para obter o valor de um elemento filho por nome
    private static String getTagValue(Element parent, String tag) {
        if (parent.getElementsByTagName(tag).getLength() > 0) {
            return parent.getElementsByTagName(tag).item(0).getTextContent();
        }
        return null;
    }

    // Métodos utilitários adicionais podem ser facilmente adicionados aqui
}
