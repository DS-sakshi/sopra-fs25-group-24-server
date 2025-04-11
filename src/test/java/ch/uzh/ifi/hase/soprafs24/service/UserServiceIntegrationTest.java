package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the UserResource REST resource.
 *
 * @see UserService
 */
@WebAppConfiguration
@SpringBootTest
public class UserServiceIntegrationTest {

  @Qualifier("userRepository")
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserService userService;

  @BeforeEach
  public void setup() {
    userRepository.deleteAll();
  }

  @Test
  public void createUser_validInputs_success() {
    // given
    assertNull(userRepository.findByUsername("testUsername"));

    User testUser = new User();
    testUser.setName("testName");
    testUser.setUsername("testUsername");
    testUser.setPassword("password");

    // when
    User createdUser = userService.createUser(testUser);

    // then
    assertEquals(testUser.getId(), createdUser.getId());
    assertEquals(testUser.getName(), createdUser.getName());
    assertEquals(testUser.getUsername(), createdUser.getUsername());
    assertNotNull(createdUser.getToken());
    assertEquals(UserStatus.ONLINE, createdUser.getStatus());
  }

  @Test
  public void createUser_duplicateUsername_throwsException() {
    assertNull(userRepository.findByUsername("testUsername"));

    User testUser = new User();
    testUser.setName("testName");
    testUser.setUsername("testUsername");
    testUser.setPassword("password");
    User createdUser = userService.createUser(testUser);

    // attempt to create second user with same username
    User testUser2 = new User();

    // change the name but forget about the username
    testUser2.setName("testName2");
    testUser2.setUsername("testUsername");
    testUser2.setPassword("password");

    // check that an error is thrown
    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser2));
  }

    /*
   * Userstory 2
   */
  @Test
  public void logoutUser_statusOffline() {
    User testUser = new User();
    testUser.setName("testName");
    testUser.setUsername("testUsername"); 
    testUser.setPassword("password");
    testUser.setStatus(UserStatus.ONLINE);
  
    User createdUser = userService.createUser(testUser);
    userRepository.save(createdUser);
    userRepository.flush();
  
    // Verify user is online before logout
    assertEquals(UserStatus.ONLINE, createdUser.getStatus());
  

    userService.logoutUser(createdUser.getId());
    
    User userAfterLogout = userRepository.findByUsername("testUsername");
    assertNotNull(userAfterLogout, "User should exist after logout");
    assertEquals(UserStatus.OFFLINE, userAfterLogout.getStatus());
  }
  
  @Test
  public void loginUser_ValidInputs_statusToOnline() {
    User testUser = new User();
    testUser.setName("testName");
    testUser.setUsername("testUsername");
    testUser.setPassword("password");
    testUser.setStatus(UserStatus.OFFLINE);
   
    User createdUser = userService.createUser(testUser);
    createdUser.setStatus(UserStatus.OFFLINE);
    userRepository.save(createdUser);
    userRepository.flush();
  
    User userBeforeLogin = userRepository.findByUsername("testUsername");
    assertEquals(UserStatus.OFFLINE, userBeforeLogin.getStatus(), "User should be OFFLINE before login");
  
    User loggedInUser = userService.loginUser(testUser.getUsername(), testUser.getPassword());
  
    // now online after login 
    assertEquals(UserStatus.ONLINE, loggedInUser.getStatus());
  
    // Double-check by getting fresh from DB
    User userAfterLogin = userRepository.findByUsername("testUsername");
    assertEquals(UserStatus.ONLINE, userAfterLogin.getStatus());
  }
}

