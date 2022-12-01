package mx.edu.uacm.sistema.web.sistemaadopcionwebadmin.repositorio;

import mx.edu.uacm.sistema.web.sistemaadopcionwebadmin.modelo.Mascota;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface MascotaRepository extends CrudRepository<Mascota, Long> {

    @Query("SELECT u from Mascota u WHERE u.idMascota = ?1")
    public Mascota findByIdMascota(Long id);
}
//creacion repo mascotas