package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPutDTO;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


public class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserService userService;

  private User testUser;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);

    // given
    testUser = new User();
    testUser.setId(1L);
    testUser.setName("testName");
    testUser.setUsername("testUsername");
    testUser.setPassword("password");
    testUser.setToken("token123");
    testUser.setStatus(UserStatus.ONLINE);
    testUser.setCreationDate(new Date());

    // when -> any object is being save in the userRepository -> return the dummy
    // testUser
    Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
  }

  @Test
  public void createUser_validInputs_success() {
    // when -> any object is being save in the userRepository -> return the dummy
    // testUser
    User createdUser = userService.createUser(testUser);

    // then
    Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());

    assertEquals(testUser.getId(), createdUser.getId());
    assertEquals(testUser.getName(), createdUser.getName());
    assertEquals(testUser.getUsername(), createdUser.getUsername());
    assertNotNull(createdUser.getToken());
    assertEquals(UserStatus.ONLINE, createdUser.getStatus());
  }

  @Test
  public void createUser_duplicateName_throwsException() {
    // given -> a first user has already been created
    userService.createUser(testUser);

    // when -> setup additional mocks for UserRepository
    Mockito.when(userRepository.findByName(Mockito.any())).thenReturn(null);
    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

    // then -> attempt to create second user with same user -> check that an error
    // is thrown
    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
  }

  @Test
  public void createUser_duplicateInputs_throwsException() {
    // given -> a first user has already been created
    userService.createUser(testUser);

    // when -> setup additional mocks for UserRepository
    Mockito.when(userRepository.findByName(Mockito.any())).thenReturn(testUser);
    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

    // then -> attempt to create second user with same user -> check that an error
    // is thrown
    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
  }

  @Test
  public void registerUser_emptyUsername_throwsException() {
    // given
    testUser.setUsername("");

    // when
    Mockito.when(userRepository.findByName(Mockito.any())).thenReturn(null);
    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(null);

    // then
    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
  }

  @Test
  public void registerUser_emptyPassword_throwsException() {
    // given
    testUser.setPassword("");

    // when
    Mockito.when(userRepository.findByName(Mockito.any())).thenReturn(null);
    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(null);

    // then
    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
  }



@Test
public void createUser_emptyName_throwsException() {
    // given
    User invalidUser = new User();
    invalidUser.setName(""); // Empty name
    invalidUser.setUsername("testUser");
    invalidUser.setPassword("password");

    // when/then
    assertThrows(ResponseStatusException.class, () -> userService.createUser(invalidUser));
    verify(userRepository, never()).save(any(User.class));
}

@Test
public void isValidToken_validToken_returnsTrue() {
    // given
    when(userRepository.findByToken("validToken")).thenReturn(testUser);

    // when
    boolean result = userService.isValidToken("validToken");

    // then
    assertTrue(result);
}

@Test
public void isValidToken_userOffline_returnsFalse() {
    // given
    testUser.setStatus(UserStatus.OFFLINE);
    when(userRepository.findByToken("validToken")).thenReturn(testUser);

    // when
    boolean result = userService.isValidToken("validToken");

    // then
    assertFalse(result);
}

@Test
public void isValidToken_invalidToken_returnsFalse() {
    // given
    when(userRepository.findByToken("invalidToken")).thenReturn(null);

    // when
    boolean result = userService.isValidToken("invalidToken");

    // then
    assertFalse(result);
}

@Test
public void isValidToken_nullToken_returnsFalse() {
    // when
    boolean result = userService.isValidToken(null);

    // then
    assertFalse(result);
    verify(userRepository, never()).findByToken(anyString());
}

@Test
public void updateUser_userNotFound_throwsException() {
    // given
    when(userRepository.findById(99L)).thenReturn(Optional.empty());
    UserPutDTO userPutDTO = new UserPutDTO();
    userPutDTO.setUsername("newUsername");

    // when/then
    assertThrows(ResponseStatusException.class, () -> userService.updateUser(99L, 99L, userPutDTO));
}

@Test
public void updateUser_unauthorizedUser_throwsException() {
    // given
    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
    UserPutDTO userPutDTO = new UserPutDTO();
    userPutDTO.setUsername("newUsername");

    // when/then - Attempt to update user 1 while logged in as user 2
    assertThrows(ResponseStatusException.class, () -> userService.updateUser(1L, 2L, userPutDTO));
}

@Test
public void updateUser_usernameAlreadyExists_throwsException() {
    // given
    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
    
    User existingUser = new User();
    existingUser.setId(2L);
    existingUser.setUsername("existingUsername");
    
    when(userRepository.findByUsername("existingUsername")).thenReturn(existingUser);
    
    UserPutDTO userPutDTO = new UserPutDTO();
    userPutDTO.setUsername("existingUsername");

    // when/then
    assertThrows(ResponseStatusException.class, () -> userService.updateUser(1L, 1L, userPutDTO));
}

@Test
public void loginUser_userNotFound_throwsException() {
    // given
    when(userRepository.findByUsername("nonExistentUser")).thenReturn(null);

    // when/then
    assertThrows(ResponseStatusException.class, () -> userService.loginUser("nonExistentUser", "anyPassword"));
}

@Test
public void getUserByUsername_userNotFound_throwsException() {
    // given
    when(userRepository.findByUsername("nonExistentUser")).thenReturn(null);

    // when/then
    assertThrows(ResponseStatusException.class, () -> userService.getUserByUsername("nonExistentUser"));
}
}
