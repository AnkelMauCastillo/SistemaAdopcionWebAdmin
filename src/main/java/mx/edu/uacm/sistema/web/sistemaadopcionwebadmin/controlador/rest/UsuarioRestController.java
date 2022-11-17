package mx.edu.uacm.sistema.web.sistemaadopcionwebadmin.controlador.rest;

import mx.edu.uacm.sistema.web.sistemaadopcionwebadmin.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsuarioRestController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/admin/check_email")
    public String checkDuplicateEmail(@Param("id") Long id, @Param("email") String email){
        return usuarioService.isEmailUnique(id, email) ? "OK" : "Duplicated";
    }
}
