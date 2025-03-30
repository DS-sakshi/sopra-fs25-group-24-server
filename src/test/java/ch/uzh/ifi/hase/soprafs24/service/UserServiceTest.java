package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;


import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

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
    testUser.setCreationDate(new Date());
    testUser.setStatus(UserStatus.OFFLINE);

    // testUser is sufficient for now, maybe this for later
    // exitstingUser = new User();
    // exitstingUser.setId(2L);
    // exitstingUser.setName("existingName");
    // exitstingUser.setUsername("existingUsername");
    // exitstingUser.setPassword("existingPassword");
    // exitstingUser.setCreationDate(new Date());
    // exitstingUser.setStatus(UserStatus.OFFLINE);

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
  public void loginUser_validInputs_success() {
    // given
    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

    // when
    User loggedInUser = userService.loginUser(testUser.getUsername(), testUser.getPassword());

    // then

    assertEquals(testUser.getId(), loggedInUser.getId());
    assertEquals(testUser.getName(), loggedInUser.getName());
    assertEquals(testUser.getUsername(), loggedInUser.getUsername());
    // assertNotNull(loggedInUser.getToken()); as for now the creation does not influence the token, comment in again if later changed for saver authentication
    assertEquals(UserStatus.ONLINE, loggedInUser.getStatus());

    // verify that the userRepository was called once with any username
    Mockito.verify(userRepository, Mockito.times(1)).findByUsername(Mockito.any());
    // Verify save was called to update status
    Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());
  }

  @Test
  public void loginUser_invalidUsername_throwsException() {
    // given
    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(null);

 
    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.loginUser("NonExistentUsername", "password"));
    assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    assertTrue(exception.getReason().contains("Username not found"));
  }

  @Test
  public void loginUser_invalidPassword_throwsException() {
    // given
    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

    // when
    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.loginUser(testUser.getUsername(), "wrongPassword"));
  
    assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    assertTrue(exception.getReason().contains("Invalid password"));
  }
}