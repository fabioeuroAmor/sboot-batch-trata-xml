package br.cnpq.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

@Slf4j
@Service
public class XmlService {

    @Autowired
    RestTemplate restTemplate;

    @Value("${app.directory}")
    private String directory;

    @Value("${app.filePath}")
    private String filePath;

    @Value("${app.url}")
    private String url;


    public String processXmlFromDirectory(String fileName) {
        try {

            int indice = fileName.indexOf(".xml");

            if (indice != -1) {
                log.info("Extenção .xml encontrada no nome do arquivo.");
                fileName = fileName.replaceAll(".xml", " ");
            }

             filePath = filePath + fileName.trim() + "_UTF8.xml"; // Caminho do arquivo

            // Montar o caminho completo do arquivo XML
            File xmlFile = new File(directory, fileName.trim() + ".xml");

            // Verificar se o arquivo existe
            if (!xmlFile.exists()) {
                return "Arquivo não encontrado: " + xmlFile.getAbsolutePath();
            }

            // Ler o conteúdo do arquivo
            String xmlContent = new String(Files.readAllBytes(xmlFile.toPath()), StandardCharsets.UTF_8);

            //ISO-8859-1
            xmlContent = xmlContent.replace("ISO-8859-1", "UTF-8");


            // Remover caracteres especiais
            xmlContent = removeSpecialCharacters(xmlContent);

            writeStringToFile(xmlContent, filePath);

            enviaAoSolr(xmlContent, url);
            log.info("Tentativa de indexação via serviço, enviada com sucesso!");

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
        //xmlContent = xmlContent.replaceAll("(?s)\\s*<\\?xml version=\"1\\.0\" encoding=\"UTF-8\" standalone=\"yes\"\\?>\\s*", "");

        // Criar a entidade com o corpo e os cabeçalhos
        HttpEntity<String> requestEntity = new HttpEntity<>(xmlContent.trim(), headers);

        // Enviar a solicitação
        try {
            String response = restTemplate.postForObject(url, requestEntity, String.class);
            // Processar a resposta, se necessário
            log.info("Resposta do Solr:" + response);
        } catch (Exception e) {
            // Tratar exceções
            log.error("Erro ao enviar solicitação para o Solr: " + e.getMessage());
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