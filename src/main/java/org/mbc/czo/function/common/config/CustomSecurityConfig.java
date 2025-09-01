package org.mbc.czo.function.common.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mbc.czo.function.member.security.CustomUserDetailsService;
import org.mbc.czo.function.member.security.handler.CustomSocialLoginSuccessHandler;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;


@Log4j2 // 로그 출력용
@Configuration  // 환경설정임을 명시
@RequiredArgsConstructor // final 필드에 대한 생성자

public class  CustomSecurityConfig {
    // 스프링시큐리티 환경설정 하는 부분
    // board/list 접속시 /login 페이지로 자동 이동(시큐리티 내장된 로그인 페이지 : id (user)
    // Using generated security password: 056b482c-b7f9-4582-8a48-392bdc5e9d55(1회용)

    private final DataSource dataSource;

    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain FilterChain(HttpSecurity http) throws Exception {

        log.info("----------------CustomSecurityConfig.filterChain()----------------------");

        http.formLogin(form -> form
                .loginPage("/member/login")
                .loginProcessingUrl("/member/login")
                .defaultSuccessUrl("/", true)   // 로그인 성공 시 항상 루트로 이동
                .failureUrl("/member/login?error=true")
        );

        http.oauth2Login(httpSecurityOAuth2LoginConfigurer -> {
            httpSecurityOAuth2LoginConfigurer.loginPage("/member/login");
            httpSecurityOAuth2LoginConfigurer.successHandler(authenticationSuccessHandler()); //  소설로그인 암호 강제 변경
        });


        // http.csrf().disable()
        // 6.1 버전에서 제외 됨 (스프링 3.0이후 버전에서는 사용 안됨)
        // 람다식으로 사용할 것을 권고 함. 아래로 변경
        http.csrf(httpSecurityCsrfConfigurer -> {
            // csrf 토큰에 대한 비활성화
            // 실무에서는 사용하면 안됨
            // 프론트에 아래코드 필수
            // <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}">
            log.info("======= CSRF 비활성화 호출=======");
            httpSecurityCsrfConfigurer.disable();
        });

        http.rememberMe(httpSecurityRememberMeConfigurer -> {
            httpSecurityRememberMeConfigurer.key("SecurityRememberMeKey") // key는 개발자 맘대로(쿠키값을 인코딩시 활용)
                    .tokenRepository(persistentTokenRepository())  // 필요한 정보를 저장(하단 메서드추가)
                    .userDetailsService(customUserDetailsService)  // User 객체 이용
                    .tokenValiditySeconds(60*60*24*30); // 30일 보관
            log.info("======= 자동 로그인기법 rememberMe 호출=======");
            //                            초 분 시 일 쿠기의 maxAge()
        });

        http.logout(logout ->
                logout.logoutUrl("/member/logout")          // 로그아웃 경로
                        .logoutSuccessUrl("/")    // 로그아웃 후 이동할 페이지
                        .invalidateHttpSession(true)          // 세션 무효화
                        .deleteCookies("JSESSIONID")          // JSESSIONID 쿠키 삭제
                        .clearAuthentication(true)
        );

        http.exceptionHandling(ex ->  // 권한 없는 사람 페이지 보여 줄 때
                ex.accessDeniedPage("/access-denied")
        );

        http.authorizeHttpRequests(auth -> {   // 페이지 권한 설정
                    auth.requestMatchers("/board/register/**").hasRole("ADMIN");
                    auth.requestMatchers("/board/register").hasRole("ADMIN");
                    auth.requestMatchers("/board/modify/**").hasRole("ADMIN");
                    auth.requestMatchers("/board/remove/**").hasRole("ADMIN");
                    auth.requestMatchers(
                            "/member/login",
                            "/member/join",
                            "/member/findID",
                            "/member/findPW",
                            "/oauth2/authorization/kakao"
                    ).permitAll();
                    auth.requestMatchers("/board/list", "/board/read/**").permitAll();
                    // 이 줄을 다시 활성화
                    auth.anyRequest().authenticated();

                })

                .formLogin(Customizer.withDefaults())
                .exceptionHandling(exception ->
                        exception.accessDeniedPage("/") // ← 접근거부 페이지 지정
                );

       /* // p718 403예외처리 핸들러 사용
        http.exceptionHandling(httpSecurityExceptionHandlingConfigurer -> {
            httpSecurityExceptionHandlingConfigurer.accessDeniedHandler(accessDeniedHandler());
            // 하단에 메서드 추가
        });*/

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // css와 같이 정적 자원들에 대한 시큐리티 적용 제외
        // No security for GET /css/styles.css
        log.info("--- CustomSecurityConfig.WebSecurityCustomizer()---------");

        return (web) -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        log.info("======= 패스워드 암화기법 처리 메서드 실행 =======");
        return new BCryptPasswordEncoder(); // 해시코드로 암호화기법을 적용
    }

    @Bean // 자동로그인용 데이터베이스 처리
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        log.info("======= persistentTokenRepository 토큰생성기법 호출 =======");
        return jdbcTokenRepository;
        //https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/web/authentication/rememberme/PersistentTokenRepository.html
    }

    @Bean //커스텀 소설로그인 성공클래스 사용
    public AuthenticationSuccessHandler authenticationSuccessHandler(){
        return new CustomSocialLoginSuccessHandler(passwordEncoder());
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.sendRedirect("/access-denied");
        };
    }
}

