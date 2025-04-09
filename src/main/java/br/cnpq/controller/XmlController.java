package br.cnpq.controller;


import br.cnpq.service.XmlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/xml")
public class XmlController {

    @Autowired
    private XmlService xmlService;

    @PostMapping("/process")
    public String processXml(@RequestParam("fileName") String fileName) {
        return xmlService.processXmlFromDirectory(fileName);
    }
}