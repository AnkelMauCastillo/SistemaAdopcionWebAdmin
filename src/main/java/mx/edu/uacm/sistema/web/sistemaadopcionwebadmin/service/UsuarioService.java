package mx.edu.uacm.sistema.web.sistemaadopcionwebadmin.service;

import mx.edu.uacm.sistema.web.sistemaadopcionwebadmin.modelo.Usuario;
import mx.edu.uacm.sistema.web.sistemaadopcionwebadmin.repositorio.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;


    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public List<Usuario> listAll(){
        return (List<Usuario>) repository.findAll();
    }


    public void guardar(Usuario usuario) {
        boolean estaActualizandoElUsuario = (usuario.getIdUsuario() != null);

        if (estaActualizandoElUsuario) {
            Usuario existeUsuario = repository.findById(usuario.getIdUsuario()).get();
            if (usuario.getPassword().isEmpty()) {
                usuario.setPassword(existeUsuario.getPassword());
            }else {
                encodePassword(usuario);
            }
        } else {
            encodePassword(usuario);

        }
        repository.save(usuario);
    }

    private void encodePassword(Usuario usuario){
        String encodedPassword = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(encodedPassword);
    }

    public boolean isEmailUnique(Long id,String email){
        Usuario usuarioByEmail = repository.getUsuarioByEmailUsuario(email);
        if (usuarioByEmail == null) return true;

        boolean estaCreandoNuevo = (id == null);

        if (estaCreandoNuevo) {
            if (usuarioByEmail != null) return false;
        }else {
            if (usuarioByEmail.getIdUsuario() != id){
                return false;
            }
        }
        return true;
    }

    public Usuario get(Long id) throws UserNotFoundException {
        try {
            return repository.findById(id).get();
        } catch (NoSuchElementException ex) {
            throw new UserNotFoundException("No se pudo encontrar ningún usuario con ID: " + id);
        }
    }

    public void delete(Long id) throws UserNotFoundException {
        Long countById = repository.countByIdUsuario(id);
        if (countById == null || countById == 0) {
            throw new UserNotFoundException("No se pudo encontrar ningún usuario con ID: " + id);
        }
        repository.deleteById(id);
        
    }
}
