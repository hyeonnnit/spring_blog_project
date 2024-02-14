package shop.mtcoding.blog._core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

@Configuration
public class SecurityConfig {
    @Bean
    public BCryptPasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.csrf(c -> c.disable());
        http.authorizeHttpRequests(a -> {
            a.requestMatchers(RegexRequestMatcher.regexMatcher("/board\\+d")).permitAll()
                    .requestMatchers("/user/**", "/board/**").authenticated()
                    .anyRequest().permitAll();
        });
        http.formLogin(f -> {
            f.loginPage("/loginForm").loginProcessingUrl("/login").defaultSuccessUrl("/")
                    .failureUrl("/loginForm");
        });
        return http.build();
    }
}
