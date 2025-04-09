package br.cnpq.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class XmlService {

    @Autowired
    RestTemplate restTemplate;


    public String processXmlFromDirectory(String fileName) {
        try {

            int indice = fileName.indexOf(".xml");

            if (indice != -1) {
                System.out.println("A substring 'frase' começa no índice: " + indice);
                fileName = fileName.replaceAll(".xml", " ");
            }

            String directory = "C:\\\\Users\\\\Fabio Engineer\\\\Documents\\\\Novoxlm";
            String filePath = "C:\\Users\\Fabio Engineer\\Documents\\NovoxlmUTF8\\" + fileName.trim() + "_UTF8.xml"; // Caminho do arquivo


            // Montar o caminho completo do arquivo XML
            File xmlFile = new File(directory, fileName.trim() + ".xml");

            // Verificar se o arquivo existe
            if (!xmlFile.exists()) {
                return "Arquivo não encontrado: " + xmlFile.getAbsolutePath();
            }

            // Ler o conteúdo do arquivo
            String xmlContent = new String(Files.readAllBytes(xmlFile.toPath()), StandardCharsets.UTF_8);

            //ISO-8859-1
            xmlContent = xmlContent.replaceAll("ISO-8859-1", "UTF-8");


            // Remover caracteres especiais
            xmlContent = removeSpecialCharacters(xmlContent);

            writeStringToFile(xmlContent, filePath);

            String url = "https://solr-novo-fomento.dev.cnpq.br/solr/curriculo/update";
            enviaAoSolr(xmlContent, url);

            System.out.println("Texto gravado com sucesso!");

            return xmlContent;
        } catch (IOException e) {
            e.printStackTrace();
            return "Erro ao processar o arquivo: " + e.getMessage();
        }
    }

    private void enviaAoSolr(String xmlContent, String url) {
        // Configurar os cabeçalhos
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML); // ou MediaType.TEXT_XML se necessário

        //Sem cabeçalho
        xmlContent = xmlContent.replaceAll("(?s)\\s*<\\?xml version=\"1\\.0\" encoding=\"UTF-8\" standalone=\"yes\"\\?>\\s*", "");

        // Criar a entidade com o corpo e os cabeçalhos
        HttpEntity<String> requestEntity = new HttpEntity<>(xmlContent.trim(), headers);

        // Enviar a solicitação
        try {
            String response = restTemplate.postForObject(url, requestEntity, String.class);
            // Processar a resposta, se necessário
            System.out.println("Resposta do Solr: " + response);
        } catch (Exception e) {
            // Tratar exceções
            System.err.println("Erro ao enviar solicitação para o Solr: " + e.getMessage());
        }
    }

    private String removeSpecialCharacters(String str) {
        // Remove caracteres especiais
        return str.replaceAll("[^\\x20-\\x7E]", "");
    }


    public static void writeStringToFile(String content, String filePath) throws IOException {
        Path path = Paths.get(filePath);
        Files.write(path, content.getBytes(StandardCharsets.UTF_8));
    }

}