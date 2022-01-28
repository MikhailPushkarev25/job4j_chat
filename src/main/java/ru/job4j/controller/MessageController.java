package ru.job4j.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.model.Message;
import ru.job4j.repository.MessageRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/message")
public class MessageController {

    private final MessageRepository messages;

    public MessageController(MessageRepository messages) {
        this.messages = messages;
    }

    @GetMapping("/")
    public List<Message> findAll() {
        return StreamSupport.stream(
                this.messages.findAll().spliterator(), false
        ).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Message> findById(@PathVariable int id) {
        var message = this.messages.findById(id);
        message.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "ResponseStatusException in findByIdMessageController"));
        return new ResponseEntity<Message>(
                message.orElse(new Message()),
                HttpStatus.OK
        );
    }

    @PostMapping("/")
    public ResponseEntity<Message> create(@RequestBody Message message) {
        if (message.getDescription() == null || message.getId() == 0) {
            throw new NullPointerException("Message and id mustn't be empty");
        }
        return new ResponseEntity<Message>(
                this.messages.save(message),
                HttpStatus.CREATED
                );
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@RequestBody Message message) {
        this.messages.save(message);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        Message message = new Message();
        if (message.getId() == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "ResponseStatusException in deleteMessageController");
        }
        message.setId(id);
        this.messages.delete(message);
        return ResponseEntity.ok().build();
    }
}
