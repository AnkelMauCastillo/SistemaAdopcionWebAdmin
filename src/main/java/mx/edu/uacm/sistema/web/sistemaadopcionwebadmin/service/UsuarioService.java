package mx.edu.uacm.sistema.web.sistemaadopcionwebadmin.service;

import mx.edu.uacm.sistema.web.sistemaadopcionwebadmin.modelo.Role;
import mx.edu.uacm.sistema.web.sistemaadopcionwebadmin.modelo.Usuario;
import mx.edu.uacm.sistema.web.sistemaadopcionwebadmin.repositorio.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    public List<Usuario> listAll(){
        return (List<Usuario>) repository.findAll();
    }


    public void guardar(Usuario usuario) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(encodedPassword);
        repository.save(usuario);
    }
}
