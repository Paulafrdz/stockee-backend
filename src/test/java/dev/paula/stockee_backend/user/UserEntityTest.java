package dev.paula.stockee_backend.user;

import dev.paula.stockee_backend.role.RoleEntity;
import dev.paula.stockee_backend.role.RoleRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class UserEntityTest {

    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final RoleRepository roleRepository = Mockito.mock(RoleRepository.class);

    @Test
    void testPersistUserWithRoles() {
        // Mock de RoleRepository
        RoleEntity role = new RoleEntity();
        role.setId(1L);
        role.setName("USER");

        when(roleRepository.save(any(RoleEntity.class))).thenReturn(role);
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(role));

        RoleEntity savedRole = roleRepository.save(role);
        Optional<RoleEntity> lookup = roleRepository.findByName("USER");

        assertThat(savedRole.getName()).isEqualTo("USER");
        assertThat(lookup).isPresent();

        // Mock de UserRepository
        UserEntity user = UserEntity.builder()
                .id(1L)
                .username("paula")
                .email("paula@example.com")
                .password("1234")
                .roles(Set.of(savedRole))
                .build();

        when(userRepository.save(any(UserEntity.class))).thenReturn(user);

        UserEntity savedUser = userRepository.save(user);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("paula");
        assertThat(savedUser.getRoles()).hasSize(1);
        assertThat(savedUser.getRoles().iterator().next().getName()).isEqualTo("USER");
    }
}
