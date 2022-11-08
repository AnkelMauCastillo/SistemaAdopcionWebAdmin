package mx.edu.uacm.sistema.web.sistemaadopcionwebadmin.controlador;

import mx.edu.uacm.sistema.web.sistemaadopcionwebadmin.modelo.Mascota;
import mx.edu.uacm.sistema.web.sistemaadopcionwebadmin.modelo.Role;
import mx.edu.uacm.sistema.web.sistemaadopcionwebadmin.modelo.Usuario;
import mx.edu.uacm.sistema.web.sistemaadopcionwebadmin.repositorio.MascotaRepository;
import mx.edu.uacm.sistema.web.sistemaadopcionwebadmin.repositorio.UsuarioRepository;
import mx.edu.uacm.sistema.web.sistemaadopcionwebadmin.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
public class UsuarioControlador {

    @Autowired
    private UsuarioRepository userRepo;

    @Autowired
    private UsuarioService service;

    //se agrego repo mascotas
    @Autowired
    private MascotaRepository mascotaRepo;

    @GetMapping("")
    public String paginaDeInicio(){
        return "index";
    }

    @GetMapping("/admin/home")
    public String verPaginaDonadorHome(Model model){
        List<Usuario> usuarioList =service.listAll();
        model.addAttribute("usuarioList",usuarioList);
        return "admin/admin_home";
    }

    @GetMapping("/admin/login")
    public String verPaginaDonadorLogin(){
        return "admin/admin_login";
    }

    @GetMapping("/admin/nuevo")
    public String nuevoUsuario(Model model){
        List<String> listaRoles = new ArrayList<String>();
        Usuario usuario = new Usuario();
        usuario.setHabilitado(true);
        agregarRoles(listaRoles);
        model.addAttribute("usuario", usuario);
        model.addAttribute("listaRoles", listaRoles);
        return "admin/admin_user_form";
    }

    @PostMapping("/admin/guardar")
    public String guardarUsuario(Usuario usuario, RedirectAttributes redirectAttributes){
        System.out.println(usuario);
        service.guardar(usuario);
        redirectAttributes.addFlashAttribute("mensaje", "El usuario se ha guardado con éxito.");
        return "redirect:/admin/home";
    }

    private static void agregarRoles(List<String> options) {
        for(Role role: Role.values()){
            if (role.toString().equals("ADMIN")) {
                options.add(role.toString());
            }
        }
    }








}
