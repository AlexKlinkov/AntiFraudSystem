package antifraud.myConfig;

import antifraud.services.UserDetailsServiceImpl;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class WebSecurityConfigurerImpl extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    public void configure(HttpSecurity http) throws Exception {
        http.httpBasic()
                .and()
                .csrf().disable()
                .headers().frameOptions().disable()
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/auth/user").permitAll()
                .antMatchers(HttpMethod.DELETE, "/api/auth/user").hasRole("ADMINISTRATOR")
                .antMatchers(HttpMethod.GET, "/api/auth/list").hasAnyRole("ADMINISTRATOR", "SUPPORT")
                .antMatchers(HttpMethod.PUT, "/api/auth/access").hasRole("ADMINISTRATOR")
                .antMatchers(HttpMethod.PUT, "/api/auth/role").hasRole("ADMINISTRATOR")
                .antMatchers(HttpMethod.POST, "/api/antifraud/transaction").hasRole("MERCHANT")
                .antMatchers(HttpMethod.GET, "/api/antifraud/history").hasRole("SUPPORT")
                .antMatchers(HttpMethod.PUT, "/api/antifraud/transaction").hasRole("SUPPORT")
                .antMatchers(HttpMethod.GET, "/api/antifraud/transaction/max_value").hasRole("SUPPORT")
                .antMatchers(HttpMethod.POST, "api/antifraud/suspicious-ip").hasRole("SUPPORT")
                .antMatchers(HttpMethod.DELETE, "api/antifraud/suspicious-ip").hasRole("SUPPORT")
                .antMatchers(HttpMethod.GET, "api/antifraud/suspicious-ip").hasRole("SUPPORT")
                .antMatchers(HttpMethod.POST, "api/antifraud/stolencard").hasRole("SUPPORT")
                .antMatchers(HttpMethod.DELETE, "api/antifraud/stolencard").hasRole("SUPPORT")
                .antMatchers(HttpMethod.GET, "api/antifraud/stolencard").hasRole("SUPPORT")
                .antMatchers("/actuator/shutdown").permitAll()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(getEncoder());
    }

    @Bean
    public PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public Jackson2ObjectMapperBuilder jacksonBuilder() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.featuresToDisable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
        return builder;
    }
}
