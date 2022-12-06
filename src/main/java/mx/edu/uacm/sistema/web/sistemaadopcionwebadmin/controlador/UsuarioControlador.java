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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        return listByPage(1, model, "nombreUsuario", "asc", null);
    }

    @GetMapping("/admin/login")
    public String verPaginaDonadorLogin(){
        return "admin/admin_login";
    }

    @GetMapping("/admin/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") int pageNum, Model model,
                             @Param("sortField") String sortField, @Param("sortDir") String sortDir,
                             @Param("keyword") String keyword){

        System.out.println("Sort Field: " + sortField);
        System.out.println("Sort Dir: " + sortDir);
        Page<Usuario> usuarioPage= service.listByPage(pageNum, sortField, sortDir, keyword);
        List<Usuario> usuarioList = usuarioPage.getContent();
        System.out.println("Pagenum = " + pageNum);
        System.out.println("Total de Elementos = " + usuarioPage.getTotalElements());
        System.out.println("Total de paginas = " + usuarioPage.getTotalPages());
        long starCount = (pageNum - 1) * service.USUARIOS_POR_PAGINA + 1;
        long endCount = starCount + service.USUARIOS_POR_PAGINA - 1;

        if (endCount > usuarioPage.getTotalElements()) {
            endCount = usuarioPage.getTotalElements();
        }

        String reverseSort = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", usuarioPage.getTotalPages());
        model.addAttribute("starCount", starCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", usuarioPage.getTotalElements());
        model.addAttribute("usuarioList", usuarioList);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSort", reverseSort);
        model.addAttribute("keyword", keyword);
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

    @GetMapping("/admin/usuarios_alta")
    public String graficaBarra(Model model){
        List<Usuario> usuarios = service.listAll();
        Map<String, Integer> usuariosMap = new LinkedHashMap<>();
        Map<String, Integer> usuariosMapEdad = new LinkedHashMap<>();
        Map<String, Integer> usuariosMapEdadByRol = new LinkedHashMap<>();
        int contadorDonador = 0;
        int contadorAdmin = 0;
        int contadorCandidato = 0;
        List<Usuario> sumaAdmin = new ArrayList<>();
        List<Usuario> sumaDonador = new ArrayList<>();
        List<Usuario> sumaCandidato = new ArrayList<>();
        int sumaA = 0;
        int sumaB = 0;
        int sumaC = 0;
        int sumaD = 0;
        int sumaE = 0;
        int sumaF = 0;
        int sumaG = 0;
        int sumaH = 0;
        int sumaI = 0;


        for (Usuario usuario : usuarios){
            System.out.println(usuario.getRole());
            if (usuario.getRole().toString().equals("ADMIN")) {
                sumaAdmin.add(usuario);

                contadorAdmin++;
                if (usuario.getEdadUsuario()>= 18 && usuario.getEdadUsuario() < 20) {
                    sumaA++;


                } else if (usuario.getEdadUsuario()>= 20 && usuario.getEdadUsuario() < 30) {
                    sumaB++;

                }else if (usuario.getEdadUsuario()>= 30 && usuario.getEdadUsuario() < 40) {
                    sumaC++;

                }else if (usuario.getEdadUsuario()>= 40 && usuario.getEdadUsuario() < 50) {
                    sumaD++;

                }else if (usuario.getEdadUsuario()>= 50 && usuario.getEdadUsuario() < 60) {
                    sumaE++;

                }
                else if (usuario.getEdadUsuario()>= 60 && usuario.getEdadUsuario() < 70) {
                    sumaF++;

                }else if (usuario.getEdadUsuario()>= 70 && usuario.getEdadUsuario() < 80) {
                    sumaG++;

                }else if (usuario.getEdadUsuario()>= 80 && usuario.getEdadUsuario() < 90) {
                    sumaH++;

                }else if (usuario.getEdadUsuario()>= 90 && usuario.getEdadUsuario() < 100) {
                    sumaI++;

                }
            } else if (usuario.getRole().toString().equals("DONADOR")) {
                sumaDonador.add(usuario);
                contadorDonador++;
                if (usuario.getEdadUsuario()>= 18 && usuario.getEdadUsuario() < 20) {
                    sumaA++;


                } else if (usuario.getEdadUsuario()>= 20 && usuario.getEdadUsuario() < 30) {
                    sumaB++;

                }else if (usuario.getEdadUsuario()>= 30 && usuario.getEdadUsuario() < 40) {
                    sumaC++;

                }else if (usuario.getEdadUsuario()>= 40 && usuario.getEdadUsuario() < 50) {
                    sumaD++;

                }else if (usuario.getEdadUsuario()>= 50 && usuario.getEdadUsuario() < 60) {
                    sumaE++;

                }
                else if (usuario.getEdadUsuario()>= 60 && usuario.getEdadUsuario() < 70) {
                    sumaF++;

                }else if (usuario.getEdadUsuario()>= 70 && usuario.getEdadUsuario() < 80) {
                    sumaG++;

                }else if (usuario.getEdadUsuario()>= 80 && usuario.getEdadUsuario() < 90) {
                    sumaH++;

                }else if (usuario.getEdadUsuario()>= 90 && usuario.getEdadUsuario() < 100) {
                    sumaI++;

                }

            } else if (usuario.getRole().toString().equals("CANDIDATO")) {
                sumaCandidato.add(usuario);
                contadorCandidato++;
                if (usuario.getEdadUsuario()>= 18 && usuario.getEdadUsuario() < 20) {
                    sumaA++;


                } else if (usuario.getEdadUsuario()>= 20 && usuario.getEdadUsuario() < 30) {
                    sumaB++;

                }else if (usuario.getEdadUsuario()>= 30 && usuario.getEdadUsuario() < 40) {
                    sumaC++;

                }else if (usuario.getEdadUsuario()>= 40 && usuario.getEdadUsuario() < 50) {
                    sumaD++;

                }else if (usuario.getEdadUsuario()>= 50 && usuario.getEdadUsuario() < 60) {
                    sumaE++;

                }
                else if (usuario.getEdadUsuario()>= 60 && usuario.getEdadUsuario() < 70) {
                    sumaF++;

                }else if (usuario.getEdadUsuario()>= 70 && usuario.getEdadUsuario() < 80) {
                    sumaG++;

                }else if (usuario.getEdadUsuario()>= 80 && usuario.getEdadUsuario() < 90) {
                    sumaH++;

                }else if (usuario.getEdadUsuario()>= 90 && usuario.getEdadUsuario() < 100) {
                    sumaI++;

                }
            }
        }
        System.out.println("Total de Usuarios: " + usuarios.size());
        System.out.println("Admin: " + contadorAdmin);
        System.out.println("Candidato: " + contadorCandidato);
        System.out.println("Donador: " + contadorDonador);
        usuariosMap.put("DONADOR", contadorDonador);
        usuariosMap.put("CANDIDATO", contadorCandidato);
        usuariosMap.put("ADMIN", contadorAdmin);
        usuariosMapEdad.put("18-19", sumaA);
        usuariosMapEdad.put("20-29", sumaB);
        usuariosMapEdad.put("30-39", sumaC);
        usuariosMapEdad.put("40-49", sumaD);
        usuariosMapEdad.put("50-59", sumaE);
        usuariosMapEdad.put("60-69", sumaF);
        usuariosMapEdad.put("70-79", sumaG);
        usuariosMapEdad.put("80-89", sumaH);
        usuariosMapEdad.put("90-99", sumaI);
        usuariosMapEdadByRol.put("Donador", sumaDonador.size());
        usuariosMapEdadByRol.put("Candidato", sumaCandidato.size());
        usuariosMapEdadByRol.put("Admin", sumaAdmin.size());
        model.addAttribute("usuariosMapEdadByRol", usuariosMapEdadByRol);
        model.addAttribute("usuariosMapEdad", usuariosMapEdad);
        model.addAttribute("usuariosMap", usuariosMap);


        return "/admin/barra_usuarios";
    }

    @GetMapping("/admin/mascotas_alta")
    public String altaMascotas(Model model){
        List<Mascota> mascotas = (List<Mascota>) mascotaRepo.findAll();
        List<Mascota> mascotasTipoGato = new ArrayList<>();
        List<Mascota> mascotasTipoPerro = new ArrayList<>();
        for (Mascota mascota : mascotas){
            if (mascota.getTipoDeMascota().contains("Gato")) {
                mascotasTipoGato.add(mascota);
            }else{
                mascotasTipoPerro.add(mascota);
            }
        }
        System.out.println("Perros" + mascotasTipoPerro.size());
        System.out.println("Gatos" + mascotasTipoGato.size());
        model.addAttribute("Gatos", mascotasTipoGato.size());
        model.addAttribute("Perros", mascotasTipoPerro.size());
        System.out.println(mascotas);
        return "/admin/pie_mascota";
    }


}
