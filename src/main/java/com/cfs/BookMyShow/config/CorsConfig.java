package com.cfs.BookMyShow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {  //this class is for configguration in spring security  so @Configuration and @Bean always apply to method but only for that method who are under class who annoted with @Configuration
@Bean // bean because the object of this is created by ioc in spring so @Bean and  see note bean is just object  we have to provide that object to spring so that it  use it so we have give spring create bean of this method and spring will use this bean  spring ke pass yeh bean kaise jayge aby @Bean on  method
    public WebMvcConfigurer corsConfigMapping(){ //i make diffrent method name it is this but i make corsConfig to corsconfigMapping because the class name is also same and when the bean is created it also created by that name
    return new WebMvcConfigurer() {
        @Override
        public void addCorsMappings(CorsRegistry registry){
            registry.addMapping("/api/**")//“CORS rules apply to all backend endpoints starting with /api/”
                    .allowedMethods("GET","POST","PUT","DELETE")
                    .allowedHeaders("*");
        }
};
}
}
