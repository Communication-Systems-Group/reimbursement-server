package ch.uzh.csg.reimbursement.server.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "ch.uzh.csg.reimbursement.server" })
public class SpringMvcConfiguration {

}
