package br.cnpq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SbootBatchTrataXmlApplication {

	public static void main(String[] args) {
		SpringApplication.run(SbootBatchTrataXmlApplication.class, args);
	}

//	@Bean
//	public CommandLineRunner commandLineRunner(SbootBatchTrataXmlApplication sbootBatchTrataXmlApplication) {
//		return args -> {
//			String fileName = "meuArquivo"; // Exemplo de nome de arquivo
//			sbootBatchTrataXmlApplication.processFile(fileName); // Chamando o método de processamento
//		};
//	}

}
