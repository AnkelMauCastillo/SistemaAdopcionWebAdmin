package mx.edu.uacm.sistema.web.sistemaadopcionwebadmin.seguridad;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String dirName = "user-photos";
        Path usuarioFotosDir = Paths.get(dirName);

        String usuarioFotosPath = usuarioFotosDir.toFile().getAbsolutePath();

        registry.addResourceHandler("/" + dirName + "/**")
                .addResourceLocations("file:/" + usuarioFotosPath + "/");
    }
}
