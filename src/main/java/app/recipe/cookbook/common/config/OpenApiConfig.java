package app.recipe.cookbook.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI recipeOpenAPI() {
        final Server devServer = new Server();
        devServer.setUrl("http://localhost:8080");
        devServer.setDescription("Server URL in Development environment");

        final Server prodServer = new Server();
        prodServer.setUrl("https://cookbook-api.example.com");
        prodServer.setDescription("Server URL in Production environment");

        final Contact contact = new Contact();
        contact.setEmail("resteban@github");
        contact.setName("Dev Team");
        contact.setUrl("https://www.demo-rjesteban.com");

        final License license = new License().name("MIT");

        final Info info = new Info()
                .title("Recipe Cookbook API")
                .version("1.0")
                .contact(contact)
                .description("A RESTful API that allows users to manage cooking recipes. " +
                           "You can create, read, update, and delete recipes along with their ingredients and instructions. " +
                           "The API also supports advanced recipe search with various filters.")
                .termsOfService("https://www.recipe-cookbook.com/terms")
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer, prodServer));
    }
}