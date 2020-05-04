package org.atom.login.config;

import org.atom.login.exception.JwtAuthenticationEntryPointException;
import org.atom.login.filters.JwtRequestFilter;
import org.h2.server.web.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.BeanIds;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{
	@Autowired
	private UserDetailsService myUserDetailsService;
	@Autowired
	private JwtRequestFilter jwtRequestFilter;

	@Autowired
	private JwtAuthenticationEntryPointException unAuthorized;
	
	private static final Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);

	/*@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(myUserDetailsService).passwordEncoder(passwordEncoder());
	}*/
	
	@Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
		
        try {
        	logger.trace(myUserDetailsService.toString());
			authenticationManagerBuilder
			        .userDetailsService(myUserDetailsService)
			        .passwordEncoder(passwordEncoder());
		} catch (Exception e) {
			logger.error("Faield due to", e.getCause());
		}
        logger.info("Successfully configure authantication manager builder");
    }

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	@Bean(BeanIds.AUTHENTICATION_MANAGER)
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.
		cors().disable().
		csrf().disable().
		authorizeRequests().
		antMatchers("/api/users/**").hasRole("ADMIN").
		antMatchers("/h2/**").permitAll().
		antMatchers("/api/auth/**").permitAll().
		anyRequest().authenticated().and().
		exceptionHandling().
		authenticationEntryPoint(unAuthorized).
		and().sessionManagement()
		.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);


		//Config for H2 DB only 
		httpSecurity.headers().frameOptions().disable();
		logger.info("Successfully configure http security");
	}


	/*
	 * Config for H2 DB only
	 * */
	@Bean
	ServletRegistrationBean h2servletRegistration(){
		ServletRegistrationBean registrationBean = new ServletRegistrationBean( new WebServlet());
		registrationBean.addUrlMappings("/h2/*");
		return registrationBean;
	}





}
