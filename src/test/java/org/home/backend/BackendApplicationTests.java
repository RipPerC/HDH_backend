package org.home.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "app.security.jwt.issuer-uri=https://issuer.example.com",
        "app.security.jwt.jwk-set-uri=https://issuer.example.com/keys",
        "app.security.jwt.audience=hdh-api"
})
class BackendApplicationTests {

    @Test
    void contextLoads() {
    }

}
