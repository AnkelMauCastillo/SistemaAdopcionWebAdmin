package mx.edu.uacm.sistema.web.sistemaadopcionwebadmin.controlador;

import mx.edu.uacm.sistema.web.sistemaadopcionwebadmin.modelo.Mascota;
import mx.edu.uacm.sistema.web.sistemaadopcionwebadmin.modelo.Role;
import mx.edu.uacm.sistema.web.sistemaadopcionwebadmin.modelo.Usuario;
import mx.edu.uacm.sistema.web.sistemaadopcionwebadmin.repositorio.MascotaRepository;
import mx.edu.uacm.sistema.web.sistemaadopcionwebadmin.repositorio.UsuarioRepository;
import mx.edu.uacm.sistema.web.sistemaadopcionwebadmin.service.UserNotFoundException;
import mx.edu.uacm.sistema.web.sistemaadopcionwebadmin.service.UsuarioService;
import mx.edu.uacm.sistema.web.sistemaadopcionwebadmin.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Date;
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
        return listByPage(1, model);
    }

    @GetMapping("/admin/login")
    public String verPaginaDonadorLogin(){
        return "admin/admin_login";
    }

    @GetMapping("/admin/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") int pageNum, Model model){
        Page<Usuario> usuarioPage= service.listByPage(pageNum);
        List<Usuario> usuarioList = usuarioPage.getContent();
        System.out.println("Pagenum = " + pageNum);
        System.out.println("Total de Elementos = " + usuarioPage.getTotalElements());
        System.out.println("Total de paginas = " + usuarioPage.getTotalPages());
        long starCount = (pageNum - 1) * service.USUARIOS_POR_PAGINA + 1;
        long endCount = starCount + service.USUARIOS_POR_PAGINA - 1;

        if (endCount > usuarioPage.getTotalElements()) {
            endCount = usuarioPage.getTotalElements();
        }

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", usuarioPage.getTotalPages());
        model.addAttribute("starCount", starCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", usuarioPage.getTotalElements());
        model.addAttribute("usuarioList",usuarioList);
        return "admin/admin_home";
    }

    @GetMapping("/admin/nuevo")
    public String nuevoUsuario(Model model){
        List<String> listaRoles = new ArrayList<String>();
        Usuario usuario = new Usuario();
        usuario.setHabilitado(true);
        agregarRoles(listaRoles);
        model.addAttribute("usuario", usuario);
        model.addAttribute("listaRoles", listaRoles);
        model.addAttribute("pageTitle", "Crear Nuevo Usuario");
        return "admin/admin_user_form";
    }

    @PostMapping("/admin/guardar")
    public String guardarUsuario(Usuario usuario, RedirectAttributes redirectAttributes,
                                 @RequestParam("image") MultipartFile multipartFile) throws IOException {
        if (!multipartFile.isEmpty()) {
            LocalDate usuarioFechaNacimiento= usuario.getFechaNcimientoUsuario();
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            usuario.setIdentificacionOficialFile(fileName);
            usuario.setEdadUsuario(Period.between(usuarioFechaNacimiento, LocalDate.now()).getYears());
            Usuario savedUser = service.guardar(usuario);
            String uploadDir = "user-photos/" + savedUser.getIdUsuario();

            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

        } else {
            if (usuario.getIdentificacionOficialFile().isEmpty()) usuario.setIdentificacionOficialFile(null);
            service.guardar(usuario);
        }
        System.out.println(usuario);
        System.out.println(multipartFile.getOriginalFilename());



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

    @GetMapping("/admin/editar/{idUsuario}")
    public String editarUsuario(@PathVariable(name = "idUsuario") Long idUsuario,Model model ,RedirectAttributes redirectAttributes){
        try {
            Usuario usuario = service.get(idUsuario);
            String listRoles = usuario.getRole().toString();
            model.addAttribute("usuario",usuario);
            model.addAttribute("listaRoles", listRoles);
            model.addAttribute("pageTitle", "Editar Usuario (ID: " + idUsuario + ")");
            return "admin/admin_user_form";
        } catch (UserNotFoundException ex) {
            redirectAttributes.addFlashAttribute("mensaje", ex.getMessage());
            return "redirect:/admin/home";
        }

    }

    @GetMapping("/admin/eliminar/{idUsuario}")
    public String eliminarUsuario(@PathVariable(name = "idUsuario") Long idUsuario,Model model ,RedirectAttributes redirectAttributes){
        try {
            service.delete(idUsuario);
            redirectAttributes.addFlashAttribute("mensaje", "El usuario ID " + idUsuario + " ha sido eliminado con exito");

        } catch (UserNotFoundException ex) {
            redirectAttributes.addFlashAttribute("mensaje", ex.getMessage());
        }
        return "redirect:/admin/home";
    }

    @GetMapping("/admin/{idUsuario}/habilitado/{status}")
    public String updateUsuarioEnabledStatus(@PathVariable(name = "idUsuario") Long idUsuario, @PathVariable("status") boolean habilitado, RedirectAttributes redirectAttributes){
        service.updateUsuarioEnabledStatus(idUsuario, habilitado);
        String status = (habilitado) ? "habilitado" : "deshabilitado";
        String mensaje = "El usuario con el id " + idUsuario + "ha sido " + status;
        redirectAttributes.addFlashAttribute("mensaje",mensaje);
        return "redirect:/admin/home";

    }


}
