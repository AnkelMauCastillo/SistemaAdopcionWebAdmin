package mx.edu.uacm.sistema.web.sistemaadopcionwebadmin.modelo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Table(name = "mascotas")
public class Mascota {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMascota;
    private String nombreMascota;
    private  String sexoMascota;
    private Long edadMascota;
    private Double pesoMascota;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;


    public Mascota() {

    }

    public Mascota(String nombreMascota, String sexoMascota, Long edadMascota, Double pesoMascota) {
        this.nombreMascota = nombreMascota;
        this.sexoMascota = sexoMascota;
        this.edadMascota = edadMascota;
        this.pesoMascota = pesoMascota;
    }
}
