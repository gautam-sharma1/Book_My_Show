package com.cfs.BookMyShow.config;

import org.springframework.context.annotation.Configuration;

/*
 THIS CLASS IS NOW DISABLED - its job moved into SecurityConfig.

 WHY? Spring Security's filter chain runs BEFORE Spring MVC. A CORS setup written
 as a WebMvcConfigurer (like the one below) lives inside MVC, so the browser's
 pre-flight OPTIONS request gets rejected with 401 by the security filters before
 MVC is ever reached. The CORS rules would simply never run.

 The fix is to register CORS as a CorsConfigurationSource bean and wire it into the
 filter chain with http.cors(...). That now lives in SecurityConfig.

 The old code is kept below, commented out, with all your original notes intact.

 One extra problem with the old version worth knowing: it set allowedMethods but
 never set allowedOrigins, and it did not allow the OPTIONS method - which is the
 method browsers use for the pre-flight check. The new version in SecurityConfig
 sets origins, methods (including OPTIONS), headers, and exposes the Authorization
 header so JavaScript can actually read the token back.
*/
@Configuration
public class CorsConfig {  //this class is for configguration in spring security  so @Configuration and @Bean always apply to method but only for that method who are under class who annoted with @Configuration

// OLDER CODE (replaced by corsConfigurationSource() in SecurityConfig). Kept for reference:
//
//@Bean // bean because the object of this is created by ioc in spring so @Bean and  see note bean is just object  we have to provide that object to spring so that it  use it so we have give spring create bean of this method and spring will use this bean  spring ke pass yeh bean kaise jayge aby @Bean on  method
//    public WebMvcConfigurer corsConfigMapping(){ //i make diffrent method name it is this but i make corsConfig to corsconfigMapping because the class name is also same and when the bean is created it also created by that name
//    return new WebMvcConfigurer() {
//        @Override
//        public void addCorsMappings(CorsRegistry registry){
//            registry.addMapping("/api/**")//“CORS rules apply to all backend endpoints starting with /api/”
//                    .allowedMethods("GET","POST","PUT","DELETE")
//                    .allowedHeaders("*");
//        }
//};
//}
}
