package mx.edu.uacm.sistema.web.sistemaadopcionwebadmin.repositorio;

import mx.edu.uacm.sistema.web.sistemaadopcionwebadmin.modelo.Mascota;
import org.springframework.data.repository.CrudRepository;

public interface MascotaRepository extends CrudRepository<Mascota, Long> {
}
//creacion repo mascotas