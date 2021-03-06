package ru.job4j.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.model.User;
import ru.job4j.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository users;
    private final BCryptPasswordEncoder encoder;
    private final ObjectMapper objectMapper;

    private final static Logger LOGGER = LoggerFactory.getLogger(UserController.class.getSimpleName());

    public UserController(UserRepository users, BCryptPasswordEncoder encoder, ObjectMapper objectMapper) {
        this.users = users;
        this.encoder = encoder;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/")
    public List<User> findAllUsers() {
        User user = new User();
        if (user.getRole() == null) {
            throw new IllegalArgumentException("User role, no data!");
        }
        return StreamSupport.stream(
                this.users.findAll().spliterator(), false
        ).collect(Collectors.toList());
    }

    @PostMapping("/")
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
        if (user.getId() == 0) {
            throw new IllegalArgumentException("User is missing");
        }
        return new ResponseEntity<>(
                this.users.save(user),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@Valid @PathVariable int id) {
        var user = this.users.findById(id);
        user.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "ResponseStatusException in findByIdUserController"));
        return new ResponseEntity<User>(
                user.orElse(new User()),
                HttpStatus.OK
        );
    }

    @PutMapping("/")
    public ResponseEntity<User> update(@Valid @RequestBody User user) {
        return new ResponseEntity<>(
                this.users.save(user),
                HttpStatus.CREATED
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Valid @PathVariable int id) {
        User user = new User();
        if (user.getId() == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "ResponseStatusException in deleteUserController");
        }
        user.setId(id);
        this.users.delete(user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/sign-up")
    public void signUp(@Valid @RequestBody User user) {
        if (user.getUsername() == null || user.getPassword() == null) {
            throw new NullPointerException("Username and password mustn't be empty");
        }
        if (user.getPassword().length() < 5) {
            throw new IllegalArgumentException("Password does not match description");
        }
        user.setPassword(encoder.encode(user.getPassword()));
        users.save(user);
    }

    @GetMapping("/all")
    public Iterable<User> findAll() {
        return users.findAll();
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public void exceptionHandler(Exception e,
                                 HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(new HashMap<>() {{
            put("massage", e.getMessage());
            put("type", e.getClass());
        }}));
        LOGGER.error(e.getLocalizedMessage());
    }

    @PatchMapping("/userPatch")
    public ResponseEntity<User> patchUser(@Valid @RequestBody User user) throws InvocationTargetException, IllegalAccessException {
        var current = users.findById(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        var methods = current.getClass().getDeclaredMethods();
        var namePerMethod = new HashMap<String, Method>();
        for (var method : methods) {
            var name = method.getName();
            if (name.startsWith("get") || name.startsWith("set")) {
                namePerMethod.put(name, method);
            }
        }
        for (var name : namePerMethod.keySet()) {
            if (name.startsWith("get")) {
                var getMethod = namePerMethod.get(name);
                var setMethod = namePerMethod.get(name.replace("get", "set"));
                if (setMethod == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid properties mapping");
                }
                var newValue = getMethod.invoke(user);
                if (newValue != null) {
                    setMethod.invoke(current, newValue);
                }
            }
        }
        return new ResponseEntity<>(
                users.save(current), HttpStatus.OK
        );
    }
}
