package br.org.edu.ifrn.LojaCarro;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String caminhoAbsoluto = Paths.get("imagens").toAbsolutePath().toUri().toString();
        if (!caminhoAbsoluto.endsWith("/")) {
            caminhoAbsoluto += "/";
        }

        registry.addResourceHandler("/imagens/**")
                .addResourceLocations(caminhoAbsoluto);
    }
}