package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);


    private final UserRepository userRepository;


    @Autowired
    public UserService( @Qualifier("userRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    /**
     * This is a helper method that will check the uniqueness criteria of the
     * username and the name
     * defined in the User entity. The method will do nothing if the input is unique
     * and throw an error otherwise.
     *
     * @param userToBeCreated
     * @throws org.springframework.web.server.ResponseStatusException
     * @see User
     */
    private void checkIfUserExists(User userToBeCreated) {
        User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());

        String baseErrorMessage = "The username provided is not unique. Therefore, the user could not be created!";
        if (userByUsername != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, baseErrorMessage);
        }
    }


    public List<User> getUsers() {
        return this.userRepository.findAll();
    }

    public User getUserById(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("User with ID %d was not found", userId));
        }
        return userOptional.get();
    }

    public User getUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("User with username '%s' was not found", username));
        }
        return user;
    }

    public User createUser(User newUser) {
        // Check for empty fields
        if (newUser.getUsername() == null || newUser.getUsername().trim().isEmpty() ||
                newUser.getName() == null || newUser.getName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Username and name cannot be empty");
        }

        // For a real application, we would hash the password here
        if (newUser.getPassword() == null || newUser.getPassword().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Password cannot be empty");
        }

        newUser.setToken(UUID.randomUUID().toString());
        newUser.setStatus(UserStatus.ONLINE);
        newUser.setCreationDate(new Date());

        // Birthday is NOT set here - it can only be set on the profile page

        checkIfUserExists(newUser);

        // saves the given entity but data is only persisted in the database once
        // flush() is called
        newUser = userRepository.save(newUser);
        userRepository.flush();

        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    public User updateUser(Long userId, Long currentUserId, UserPutDTO userPutDTO) {
        // Log inputs to debug permissions
        log.info("Updating user {} by currentUser {}", userId, currentUserId);

        // Check if the current user is authorized to update this profile
        if (!userId.equals(currentUserId)) {
            log.warn("Permission denied: User {} attempted to update profile of user {}", currentUserId, userId);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You can only update your own profile");
        }

        // Get user by ID
        User user = getUserById(userId);
        log.info("Found user to update: {}", user.getUsername());

        // Check if username exists (if it's being updated)
        if (userPutDTO.getUsername() != null && !userPutDTO.getUsername().equals(user.getUsername())) {
            User existingUser = userRepository.findByUsername(userPutDTO.getUsername());
            if (existingUser != null) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Username already exists");
            }
        }

        user.setBirthday(userPutDTO.getBirthday());
        user.setUsername(userPutDTO.getUsername());

        // Save updated user
        user = userRepository.save(user);
        userRepository.flush();
        log.info("User updated successfully");

        return user;
    }

    public User loginUser(String username, String password) {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Username not found");
        }

        if (!user.getPassword().equals(password)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Invalid password");
        }

        user.setStatus(UserStatus.ONLINE);
        userRepository.save(user);
        userRepository.flush();

        return user;
    }

    public void logoutUser(Long userId) {
        User user = getUserById(userId);
        user.setStatus(UserStatus.OFFLINE);
        userRepository.save(user);
        userRepository.flush();
    }


        /**
     * Validates if the token is associated with an active user
     * 
     * @param token - The authentication token to validate
     * @return true if the token is valid, false otherwise
     */
    public boolean isValidToken(String token) {
        if (token == null || token.isEmpty()) {
            log.debug("Token validation failed: token is null or empty");
            return false;
        }
        
        // Find user with the given token
        User userByToken = userRepository.findByToken(token);
        
        // Check if user exists and is in ONLINE status
        boolean isValid = (userByToken != null && userByToken.getStatus() == UserStatus.ONLINE);
        
        if (!isValid && userByToken != null) {
            log.debug("Token validation failed: User {} is not ONLINE", userByToken.getId());
        } else if (userByToken == null) {
            log.debug("Token validation failed: No user found with token");
        }
        
        return isValid;
    }
}