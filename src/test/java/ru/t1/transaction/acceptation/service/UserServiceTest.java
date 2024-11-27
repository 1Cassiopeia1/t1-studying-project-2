package ru.t1.transaction.acceptation.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.t1.transaction.acceptation.dto.enums.Role;
import ru.t1.transaction.acceptation.model.User;
import ru.t1.transaction.acceptation.repository.UserRepository;
import ru.t1.transaction.acceptation.service.impl.security.UserServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test for UserService")
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User()
                .setId(1L)
        .setUsername("testUser ")
        .setPassword("password123")
        .setRole(Role.ROLE_USER);
    }

    @Test
    public void createUserSuccessTest() {
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User createdUser  = userService.create(user);

        assertNotNull(createdUser );
        assertEquals(user.getUsername(), createdUser .getUsername());
        verify(userRepository).save(user);
    }

    @Test
    public void createUserUsernameExistsTest() {
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(true);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.create(user);
        });

        assertEquals("Пользователь с таким именем уже существует", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void getByUsernameSuccessTest() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(java.util.Optional.of(user));

        User foundUser  = userService.getByUsername(user.getUsername());

        assertNotNull(foundUser );
        assertEquals(user.getUsername(), foundUser .getUsername());
    }

    @Test
    public void getByUsernameFailureTest() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(java.util.Optional.empty());

        Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
            userService.getByUsername(user.getUsername());
        });

        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    public void existsByUsernameTest() {
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(true);

        boolean exists = userService.existsByUsername(user.getUsername());

        assertTrue(exists);
        verify(userRepository).existsByUsername(user.getUsername());
    }
}
