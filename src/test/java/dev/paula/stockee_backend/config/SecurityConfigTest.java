package dev.paula.stockee_backend.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityConfigTest {

    @Test
    void testPasswordEncoderDirectly() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String encoded = encoder.encode("test");
        
        assertThat(encoder.matches("test", encoded)).isTrue();
        assertThat(encoder.matches("wrong", encoded)).isFalse();
    }

    @Test
    void testSecurityConfigInstantiation() {
        SecurityConfig config = new SecurityConfig();
        assertThat(config).isNotNull();
        assertThat(config.passwordEncoder()).isInstanceOf(BCryptPasswordEncoder.class);
    }
}