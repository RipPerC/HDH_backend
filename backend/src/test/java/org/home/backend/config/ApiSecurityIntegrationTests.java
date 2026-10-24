package org.home.backend.config;

import org.home.backend.catalog.film.application.FilmService;
import org.home.backend.catalog.film.domain.Film;
import org.home.backend.common.web.ApiExceptionHandler;
import org.home.backend.common.web.ProblemDetailsWriter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(properties = {
        "app.security.jwt.issuer-uri=https://issuer.example.com",
        "app.security.jwt.jwk-set-uri=https://issuer.example.com/keys",
        "app.security.jwt.audience=hdh-api",
        "app.cors.allowed-origins=https://frontend.example.com"
})
@AutoConfigureMockMvc
@Import({
        SecurityConfiguration.class,
        WebConfigurer.class,
        ApiExceptionHandler.class,
        ProblemDetailsWriter.class,
        ApiSecurityIntegrationTests.TestHealthEndpoint.class
})
class ApiSecurityIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private FilmService filmService;

    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    @Test
    void rejectsRequestsWithoutBearerTokenWithProblemDetails() throws Exception {
        mockMvc.perform(get("/v1/film"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.title").value("Unauthorized"))
                .andExpect(jsonPath("$.instance").value("/v1/film"));
    }

    @Test
    void rejectsInvalidBearerTokenWithProblemDetails() throws Exception {
        when(jwtDecoder.decode("invalid")).thenThrow(new BadJwtException("invalid token"));

        mockMvc.perform(get("/v1/film").header("Authorization", "Bearer invalid"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void rejectsExpiredBearerTokenWithProblemDetails() throws Exception {
        when(jwtDecoder.decode("expired")).thenThrow(new BadJwtException("token expired"));

        mockMvc.perform(get("/v1/film").header("Authorization", "Bearer expired"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void writesProblemDetailsForForbiddenResponses() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/v1/admin");
        MockHttpServletResponse response = new MockHttpServletResponse();

        accessDeniedHandler.handle(request, response,
                new org.springframework.security.access.AccessDeniedException("not allowed"));

        org.assertj.core.api.Assertions.assertThat(response.getStatus()).isEqualTo(403);
        org.assertj.core.api.Assertions.assertThat(response.getContentType())
                .startsWith("application/problem+json");
        org.assertj.core.api.Assertions.assertThat(response.getContentAsString())
                .contains("\"status\":403", "\"title\":\"Forbidden\"");
    }

    @Test
    void acceptsValidBearerTokenAndPreservesEmptyListResponse() throws Exception {
        when(jwtDecoder.decode("valid")).thenReturn(jwt());
        when(filmService.getAllFilms()).thenReturn(List.of());

        mockMvc.perform(get("/v1/film").header("Authorization", "Bearer valid"))
                .andExpect(status().isNoContent());
    }

    @Test
    void returnsOkForExistingFilm() throws Exception {
        when(jwtDecoder.decode("valid")).thenReturn(jwt());
        when(filmService.getById(7L)).thenReturn(new Film("Test", "Director", 2024, "EN", "ES", "digital", false));

        mockMvc.perform(get("/v1/film/7").header("Authorization", "Bearer valid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test"));
    }

    @Test
    void returnsCreatedForNewFilm() throws Exception {
        when(jwtDecoder.decode("valid")).thenReturn(jwt());

        mockMvc.perform(post("/v1/film")
                        .header("Authorization", "Bearer valid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id":0,"title":"Test","director":"Director","year":2024,
                                 "audio":"en","subtitle":"es","format":"digital","downloaded":false}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test"));
    }

    @Test
    void returnsAcceptedForFilmUpdate() throws Exception {
        when(jwtDecoder.decode("valid")).thenReturn(jwt());

        mockMvc.perform(post("/v1/film")
                        .header("Authorization", "Bearer valid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id":7,"title":"Test","director":"Director","year":2024,
                                 "audio":"en","subtitle":"es","format":"digital","downloaded":false}
                                """))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").value(7));
    }

    @Test
    void returnsAcceptedWhenDeletingExistingFilm() throws Exception {
        when(jwtDecoder.decode("valid")).thenReturn(jwt());
        when(filmService.getById(7L)).thenReturn(new Film("Test", "Director", 2024, "EN", "ES", "digital", false));

        mockMvc.perform(delete("/v1/film/7").header("Authorization", "Bearer valid"))
                .andExpect(status().isAccepted());
    }

    @Test
    void returnsProblemDetailsWhenFilmDoesNotExist() throws Exception {
        when(jwtDecoder.decode("valid")).thenReturn(jwt());
        when(filmService.getById(42L)).thenReturn(null);

        mockMvc.perform(get("/v1/film/42").header("Authorization", "Bearer valid"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Not Found"));
    }

    @Test
    void returnsProblemDetailsWhenDeletingFilmThatDoesNotExist() throws Exception {
        when(jwtDecoder.decode("valid")).thenReturn(jwt());
        when(filmService.getById(42L)).thenReturn(null);

        mockMvc.perform(delete("/v1/film/42").header("Authorization", "Bearer valid"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void returnsProblemDetailsForUnknownRoute() throws Exception {
        when(jwtDecoder.decode("valid")).thenReturn(jwt());

        mockMvc.perform(get("/v1/missing").header("Authorization", "Bearer valid"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void returnsProblemDetailsForMalformedJson() throws Exception {
        when(jwtDecoder.decode("valid")).thenReturn(jwt());

        mockMvc.perform(post("/v1/film")
                        .header("Authorization", "Bearer valid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void returnsProblemDetailsForUnsupportedMediaType() throws Exception {
        when(jwtDecoder.decode("valid")).thenReturn(jwt());

        mockMvc.perform(post("/v1/film")
                        .header("Authorization", "Bearer valid")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("film"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.status").value(415));
    }

    @Test
    void returnsProblemDetailsForUnsupportedMethod() throws Exception {
        when(jwtDecoder.decode("valid")).thenReturn(jwt());

        mockMvc.perform(put("/v1/film").header("Authorization", "Bearer valid"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.status").value(405));
    }

    @Test
    void hidesUnexpectedExceptionDetails() throws Exception {
        when(jwtDecoder.decode("valid")).thenReturn(jwt());
        when(filmService.getAllFilms()).thenThrow(new IllegalStateException("private database details"));

        mockMvc.perform(get("/v1/film").header("Authorization", "Bearer valid"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.detail").value("An unexpected error occurred."))
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("private database details"))));
    }

    @Test
    void healthPathIsPublic() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void allowsCorsPreflightForConfiguredOriginAndAuthorizationHeader() throws Exception {
        mockMvc.perform(options("/v1/film")
                        .header("Origin", "https://frontend.example.com")
                        .header("Access-Control-Request-Method", "GET")
                        .header("Access-Control-Request-Headers", "authorization"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "https://frontend.example.com"))
                .andExpect(header().string("Access-Control-Allow-Headers",
                        org.hamcrest.Matchers.containsStringIgnoringCase("authorization")));
    }

    @Test
    void doesNotAllowCorsForUnconfiguredOrigin() throws Exception {
        mockMvc.perform(options("/v1/film")
                        .header("Origin", "https://attacker.example.com")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }

    private Jwt jwt() {
        Instant now = Instant.now();
        return new Jwt("valid", now, now.plusSeconds(300),
                Map.of("alg", "RS256", "kid", "test"),
                Map.of("iss", "https://issuer.example.com", "aud", List.of("hdh-api"), "sub", "test-user"));
    }

    @RestController
    static class TestHealthEndpoint {

        @GetMapping("/actuator/health")
        Map<String, String> health() {
            return Map.of("status", "UP");
        }
    }
}
