package ru.job4j.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.model.Room;
import ru.job4j.model.User;
import ru.job4j.repository.RoomRepository;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/room")
public class RoomController {

    private final RoomRepository rooms;

    public RoomController(RoomRepository rooms) {
        this.rooms = rooms;
    }

    @GetMapping("/")
    public List<Room> findAll() {
        return StreamSupport.stream(
                this.rooms.findAll().spliterator(), false
        ).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Room> findById(@PathVariable int id) {
        var room = this.rooms.findById(id);
        room.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "ResponseStatusException in findByIdRoomController"));
        return new ResponseEntity<>(
                room.orElse(new Room()),
                HttpStatus.OK
        );
    }

    @PostMapping("/")
    public ResponseEntity<Room> create(@RequestBody Room room) {
        if (room.getNames() == null || room.getId() == 0) {
            throw new NullPointerException("Room and id mustn't be empty");
        }
        return new ResponseEntity<>(
                this.rooms.save(room),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@RequestBody Room room) {
        this.rooms.save(room);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public  ResponseEntity<Void> delete(@PathVariable int id) {
        Room room = new Room();
        if (room.getId() == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "ResponseStatusException in deleteRoomController");
        }
        room.setId(id);
        this.rooms.delete(room);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/roomPatch")
    public ResponseEntity<Room> patchUser(@RequestBody Room room) throws InvocationTargetException, IllegalAccessException {
        var current = rooms.findById(room.getId())
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
                var newValue = getMethod.invoke(room);
                if (newValue != null) {
                    setMethod.invoke(current, newValue);
                }
            }
        }
        return new ResponseEntity<>(
                rooms.save(current), HttpStatus.OK
        );
    }
}
