package mx.edu.uacm.sistema.web.sistemaadopcionwebadmin;

import mx.edu.uacm.sistema.web.sistemaadopcionwebadmin.modelo.Mascota;
import mx.edu.uacm.sistema.web.sistemaadopcionwebadmin.modelo.Role;
import mx.edu.uacm.sistema.web.sistemaadopcionwebadmin.modelo.Usuario;
import mx.edu.uacm.sistema.web.sistemaadopcionwebadmin.repositorio.MascotaRepository;
import mx.edu.uacm.sistema.web.sistemaadopcionwebadmin.repositorio.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Rollback;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class AdminTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    @Test
    public void testCreateUserAdmin() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        Usuario user = new Usuario();
        user.setEmailUsuario("eduardo@uacm.edu.mx");
        user.setPassword(passwordEncoder.encode("123456"));
        user.setNombreUsuario("EDuardo");
        //user.setIdRolUsuario(1);
        user.setRole(Role.ADMIN);


        Usuario savedUser = usuarioRepository.save(user);

        Usuario existUser = entityManager.find(Usuario.class, savedUser.getIdUsuario());

        assertThat(user.getEmailUsuario()).isEqualTo(existUser.getEmailUsuario());

    }

    @Test
    public void testGetUsuarioByEmail(){
        String email = "aa@gmail.com";
        Usuario usuario =usuarioRepository.getUsuarioByEmailUsuario(email);
        assertThat(usuario).isNotNull();

    }



    @Test
    public void testCountById(){
        Long id = 3L;
        Long counById = usuarioRepository.countByIdUsuario(id);
        assertThat(counById).isNotNull().isGreaterThan(0);
    }

    @Test
    public void testDisableUser(){
        Long id = 1L;
        usuarioRepository.updateEnableHabilidato(id, false);
    }

    @Test
    public void testEnableUser(){
        Long id = 1L;
        usuarioRepository.updateEnableHabilidato(id, true);
    }

    @Test
    public void testListFirstPage(){
        int pageNumber = 1;
        int pageSize = 4;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Usuario> page =  usuarioRepository.findAll(pageable);

        List<Usuario> listUsuarios =  page.getContent();
        listUsuarios.forEach(System.out::println);

        assertThat(listUsuarios.size()).isEqualTo(pageSize);
    }

    @Test
    public void testSearchUsuarios(){
        String keyword = "Zavala";

        int pageNumber = 0;
        int pageSize = 4;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Usuario> page =  usuarioRepository.findAll(keyword, pageable);

        List<Usuario> listUsuarios =  page.getContent();
        listUsuarios.forEach(usuario -> System.out.println(usuario));

        assertThat(listUsuarios.size()).isGreaterThan(0);
    }

    @Test
    public void testAddMascota(){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        Usuario user = new Usuario();
        user.setEmailUsuario("eduardo@uacm.edu.mx");
        user.setPassword(passwordEncoder.encode("123456"));
        user.setNombreUsuario("EDuardo");
        //user.setIdRolUsuario(1);
        user.setRole(Role.ADMIN);


        Usuario savedUser = usuarioRepository.save(user);

        Mascota mascota = new Mascota("Peluche", "Maxcho", 12L,23.4);
        mascota.addUsuario(savedUser);

        mascotaRepository.save(mascota);


    }

    @Test
    public void testAddUsuario(){
        Mascota mascota = mascotaRepository.findByIdMascota(1l);
        System.out.println(mascota);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        Usuario user = new Usuario();
        user.setEmailUsuario("bernarrdo@uacm.edu.mx");
        user.setPassword(passwordEncoder.encode("123456"));
        user.setNombreUsuario("Bernado");
        //user.setIdRolUsuario(1);
        user.setRole(Role.ADMIN);
        user.addMascotas(mascota);
        Usuario savedUser = usuarioRepository.save(user);
        mascota.addUsuario(savedUser);
        mascotaRepository.save(mascota);

    }



}
