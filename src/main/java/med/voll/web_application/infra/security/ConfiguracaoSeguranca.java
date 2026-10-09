package med.voll.web_application.infra.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration     // Indica ao Spring que esta classe contém definições de Beans e configurações da aplicação
@EnableWebSecurity // Habilita a segurança web do Spring Security e integra as configurações com o Spring MVC
public class ConfiguracaoSeguranca {

    @Bean // Registra este metodo como um Bean para o Spring Security reconhecer o serviço de usuários
    public UserDetailsService dadosUsuariosCadastrado() {
        // Cria o primeiro usuário com suas credenciais de acesso
        UserDetails usuario1 = User.builder()
                .username("lucas@vollmed.com") // Define o identificador/login do usuário
                .password("{noop}lucas123")    // Senha em texto puro. O prefixo {noop} avisa o Spring para não exigir hash/criptografia
                .build();                       // Constrói a instância de UserDetails do primeiro usuário

        UserDetails usuario2 = User.builder()
                .username("maria@vollmed.com")
                .password("{noop}maria123")
                .build();

        // Retorna a implementação que armazena esses usuários na memória RAM (ideal para testes/desenvolvimento)
        return new InMemoryUserDetailsManager(usuario1, usuario2);
    }

    @Bean // Define o filtro de segurança do Spring Security como um Bean gerenciado
    public SecurityFilterChain filtroSeguranca(HttpSecurity http) throws Exception {
        return http
                // Configura as regras de autorização de acesso para as requisições HTTP
                .authorizeHttpRequests(req -> {
                    // Libera acesso público (sem necessidade de login) para arquivos estáticos (CSS, JS, imagens/assets)
                    req.requestMatchers("/css/**", "/js/**", "/assets/**").permitAll();

                    // Exige que qualquer outra requisição/rota da aplicação esteja devidamente autenticada
                    req.anyRequest().authenticated();
                })
                // Habilita e configura a autenticação baseada em formulário de login
                .formLogin(form -> form
                        // Define a URL da página personalizada de login (substitui a tela padrão do Spring Security)
                        .loginPage("/login")

                        // Redireciona o usuário para a página inicial ("/") após realizar o login com sucesso
                        .defaultSuccessUrl("/")

                        // Permite o acesso público e sem autenticação à página de login e ao endpoint do formulário
                        .permitAll()
                )
                // Habilita e configura o encerramento de sessão (logout) do usuário
                .logout(logout -> logout
                        // Redireciona o usuário para a página de login com o parâmetro "?logout" na URL após deslogar com sucesso
                        .logoutSuccessUrl("/login?logout")

                        // Permite o acesso público e sem autenticação à página de login e ao endpoint do formulário
                        .permitAll()
                )
                // Constrói e retorna a cadeia de filtros de segurança configurada
                .build();
    }
}
