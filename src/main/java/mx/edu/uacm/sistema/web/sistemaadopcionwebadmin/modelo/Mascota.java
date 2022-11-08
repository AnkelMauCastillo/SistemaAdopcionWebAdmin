package mx.edu.uacm.sistema.web.sistemaadopcionwebadmin.modelo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
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


    public Mascota() {

    }
}
