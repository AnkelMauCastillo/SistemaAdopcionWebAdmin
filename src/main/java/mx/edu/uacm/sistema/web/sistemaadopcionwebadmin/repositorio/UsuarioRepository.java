package mx.edu.uacm.sistema.web.sistemaadopcionwebadmin.repositorio;

import mx.edu.uacm.sistema.web.sistemaadopcionwebadmin.modelo.Usuario;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, Long> {

    @Query("select u from  Usuario u WHERE u.emailUsuario = ?1")
    public Usuario findByEmail(String email);

    @Query("SELECT u from Usuario u WHERE u.emailUsuario = :email")
    public Usuario getUsuarioByEmailUsuario(@Param("email") String email);

    public Long countByIdUsuario(Long idUsuario);
}
