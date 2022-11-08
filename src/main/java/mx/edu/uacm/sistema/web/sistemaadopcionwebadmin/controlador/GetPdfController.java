package mx.edu.uacm.sistema.web.sistemaadopcionwebadmin.controlador;

import mx.edu.uacm.sistema.web.sistemaadopcionwebadmin.service.CreatePdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GetPdfController {

    private CreatePdfService createPdfService;

    @Autowired
    public GetPdfController(CreatePdfService createPdfService) {
        this.createPdfService = createPdfService;
    }

    @GetMapping("/pdfFile")
    public String getPdf(){
        return "getPdfFile";
    }
    @GetMapping("/createPdf")
    public String pdfFile(){
        createPdfService.createPdf();
        return "redirect:/pdfFile";
    }

}
