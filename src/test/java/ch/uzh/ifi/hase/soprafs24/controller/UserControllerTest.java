package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
        // given
        User user = new User();
        user.setName("Firstname Lastname");
        user.setUsername("firstname@lastname");
        user.setStatus(UserStatus.OFFLINE);
        user.setCreationDate(new Date());
        String token = "1";
        user.setToken(token);

        List<User> allUsers = Collections.singletonList(user);

        // this mocks the UserService -> we define above what the userService should
        // return when getUsers() is called
        given(userService.getUsers()).willReturn(allUsers);

        // when
        MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + token);

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is(user.getName())))
                .andExpect(jsonPath("$[0].username", is(user.getUsername())))
                .andExpect(jsonPath("$[0].status", is(user.getStatus().toString())));
    }

    // Test 1: Create User (success case - 201)
    @Test
    public void createUser_validInput_userCreated() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setUsername("testUsername");
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);
        user.setCreationDate(new Date());
        user.setPassword("password");

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setName("Test User");
        userPostDTO.setUsername("testUsername");
        userPostDTO.setPassword("password");

        given(userService.createUser(Mockito.any())).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
    }

    // Test 2: Create User with duplicate username (fail case - 409)
    @Test
    public void createUser_duplicateUsername_conflict() throws Exception {
        // given
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setName("Test User");
        userPostDTO.setUsername("testUsername");
        userPostDTO.setPassword("password");

        // Setup the mock to throw a conflict exception
        doThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists"))
                .when(userService).createUser(Mockito.any());

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // Expect 409 Conflict status
        mockMvc.perform(postRequest)
                .andExpect(status().isConflict());
    }

    // Test 3: Get User by ID (success case - 200)
    @Test
    public void getUserById_validId_userReturned() throws Exception {
        // given
        String token = "1";
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setUsername("testUsername");
        user.setToken(token);
        user.setStatus(UserStatus.ONLINE);
        user.setCreationDate(new Date());

        given(userService.getUserById(Mockito.anyLong())).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder getRequest = get("/users/1")
                .contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + token);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
    }

    // Test 4: Get User by ID with invalid ID (fail case - 404)
    @Test
    public void getUserById_invalidId_notFound() throws Exception {
        // given
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User with ID 1 was not found"))
                .when(userService).getUserById(1L);

        String token = "1";

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder getRequest = get("/users/1")
                .contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + token);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isNotFound());
    }

    // Test 5: Update User (success case - 204)
    @Test
    public void updateUser_validInput_success() throws Exception {
        // given
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("newUsername");
        userPutDTO.setBirthday(new Date());
        String token = "1";


        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("CurrentUserId", "1")
                .header("Authorization", "Bearer " + token)
                 // Important: Add CurrentUserId header
                .content(asJsonString(userPutDTO));

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isNoContent());
    }

    // Test 6: Update User with invalid ID (fail case - 404)
    @Test
    public void updateUser_invalidId_notFound() throws Exception {
        // given
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("newUsername");
        userPutDTO.setBirthday(new Date());
        String token = "1";

        // Mock service to throw not found exception when user doesn't exist
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User with ID 1 was not found"))
                .when(userService).updateUser(Mockito.anyLong(), Mockito.anyLong(), Mockito.any());

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("CurrentUserId", "1")
                .header("Authorization", "Bearer " + token)// Important: Add CurrentUserId header
                .content(asJsonString(userPutDTO));

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isNotFound());
    }

        // REST Test for Userstory 1 + 2
    @Test
    public void loginUser_validCrednetials() throws Exception {
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setName("Test User");
        userPostDTO.setUsername("testUsername");
        userPostDTO.setPassword("password");

        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setUsername("testUsername");
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);
        user.setCreationDate(new Date());
        user.setPassword("password");

        given(userService.loginUser(Mockito.eq("testUsername"), Mockito.eq("password"))).willReturn(user);

        MockHttpServletRequestBuilder postRequest = post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        mockMvc.perform(postRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
    }

    @Test
    public void loginUser_wrongPassword() throws Exception {
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setName("Test User");
        userPostDTO.setUsername("testUsername");
        userPostDTO.setPassword("wrongPassword");

        doThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid password"))
                .when(userService).loginUser(Mockito.eq("testUsername"), Mockito.eq("wrongPassword"));

        MockHttpServletRequestBuilder postRequest = post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        mockMvc.perform(postRequest)
                .andExpect(status().isUnauthorized());
}

    @Test
    public void loginUser_userNotFound() throws Exception {
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setName("Test User");
        userPostDTO.setUsername("nonExistentUser");
        userPostDTO.setPassword("password");

        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"))
                .when(userService).loginUser(Mockito.eq("nonExistentUser"), Mockito.eq("password"));

        MockHttpServletRequestBuilder postRequest = post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        mockMvc.perform(postRequest)
                .andExpect(status().isNotFound());
        }

        @Test
        public void loginUser_invalidInput() throws Exception {
                UserPostDTO userPostDTO = new UserPostDTO();
                userPostDTO.setName("Test User");
                userPostDTO.setUsername("testUsername");
                userPostDTO.setPassword("");

            // Mock service to throw bad request exception when password is empty

                doThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Password cannot be empty"))
                .when(userService).loginUser(Mockito.eq("testUsername"), Mockito.eq(""));
                MockHttpServletRequestBuilder postRequest = post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userPostDTO));

                mockMvc.perform(postRequest)
                        .andExpect(status().isUnauthorized());
        }

        @Test
        public void logoutUser_successfulLogout() throws Exception {
        
        // Perform logout request
        String token = "1";
        MockHttpServletRequestBuilder postRequest = post("/logout/1").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + token);
        
        // Verify the response is No Content (204)
        mockMvc.perform(postRequest).andExpect(status().isNoContent());
        
        // Verify if correct user was logged out
        Mockito.verify(userService).logoutUser(1L);
        }


    /**
     * Helper Method to convert userPostDTO into a JSON string such that the input
     * can be processed
     * Input will look like this: {"name": "Test User", "username": "testUsername"}
     *
     * @param object
     * @return string
     */
    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("The request body could not be created.%s", e.toString()));
        }
    }
}