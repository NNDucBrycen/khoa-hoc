package com.example.demo.service;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserDetailsServiceImpl service;

    // ── Helpers ───────────────────────────────────────────────────────────────
    private User buildUser(String username, boolean enabled) {
        User u = new User();
        u.setUsername(username);
        u.setPasswordHash("$2b$12$fakehash");
        u.setEnabled(enabled);
        Role role = new Role();
        role.setCode("USER");
        role.setName("User");
        u.setRoles(Set.of(role));
        return u;
    }

    // ── Tests ─────────────────────────────────────────────────────────────────

    @Test
    void loadUserByUsername_withValidUser_returnsUserDetails() {
        given(userRepository.findByUsername("user01")).willReturn(Optional.of(buildUser("user01", true)));

        var details = service.loadUserByUsername("user01");

        assertThat(details.getUsername()).isEqualTo("user01");
        assertThat(details.isEnabled()).isTrue();
        assertThat(details.getAuthorities()).anyMatch(a -> a.getAuthority().equals("ROLE_USER"));
    }

    @Test
    void loadUserByUsername_withMissingUser_throwsUsernameNotFoundException() {
        given(userRepository.findByUsername("ghost")).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("ghost"))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void loadUserByUsername_withDisabledUser_throwsUsernameNotFoundException() {
        given(userRepository.findByUsername("disabled")).willReturn(Optional.of(buildUser("disabled", false)));

        assertThatThrownBy(() -> service.loadUserByUsername("disabled"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
