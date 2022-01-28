package ru.job4j.controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.model.Role;
import ru.job4j.repository.RoleRepository;

import java.util.List;
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
    public ResponseEntity<Role> findById(@PathVariable int id) {
        var roles = this.roles.findById(id);
        roles.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "ResponseStatusException in findByIdRolesController"));
        return new ResponseEntity<>(
                roles.orElse(new Role()),
                HttpStatus.OK
        );
    }

    @PostMapping("/")
    public ResponseEntity<Role> create(@RequestBody Role role) {
        if (role.getRoles() == null || role.getId() == 0) {
            throw new NullPointerException("Role and id mustn't be empty");
        }
        return new ResponseEntity<>(
                this.roles.save(role),
                HttpStatus.CREATED
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
}
