package ru.job4j.controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.model.Role;
import ru.job4j.model.User;
import ru.job4j.repository.RoleRepository;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/roles")
public class RoleController {

    private final RoleRepository roles;

    public RoleController(RoleRepository roles) {
        this.roles = roles;
    }


    @GetMapping("/")
    public List<Role> findAll() {
        return StreamSupport.stream(
                this.roles.findAll().spliterator(), false
        ).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable int id) {
        return ResponseEntity.of(Optional.of(new HashSet<>() {{
            add(roles.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "ResponseStatusException in findByIdRoleController")));
        }}));

    }

    @PostMapping("/")
    public ResponseEntity<?> create(@RequestBody Role role) {
        if (role.getRoles() == null || role.getId() == 0) {
            throw new NullPointerException("Role and id mustn't be empty");
        }
        Object body = new HashSet<>() {{
            add(roles.save(role));
        }};

        return new ResponseEntity(
              body,
              new MultiValueMapAdapter<>(Map.of("Job4jCostumHeader", List.of("job4j"))),
                HttpStatus.OK
        );
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@RequestBody Role role) {
                this.roles.save(role);
               return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        Role role = new Role();
        if (role.getId() == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "ResponseStatusException in deleteRoleController");
        }
        role.setId(id);
        this.roles.delete(role);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/rolePatch")
    public ResponseEntity<Role> patchUser(@RequestBody Role role) throws InvocationTargetException, IllegalAccessException {
        var current = roles.findById(role.getId())
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
                var newValue = getMethod.invoke(role);
                if (newValue != null) {
                    setMethod.invoke(current, newValue);
                }
            }
        }
        return new ResponseEntity<>(
                roles.save(current), HttpStatus.OK
        );
    }
}
