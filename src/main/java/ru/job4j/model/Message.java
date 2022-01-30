package ru.job4j.model;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull(message = "filed id not fount Message")
    private int id;
    @NotNull(message = "line not fount")
    private String description;
    @FutureOrPresent(message = "date not future")
    private Timestamp created;

    @OneToOne
    @JoinColumn(name = "room_id")
    @Valid
    private Room rooms;

    public Message() {
    }

    public static Message of(String description, Room rooms) {
        Message message = new Message();
        message.description = description;
        message.created = new Timestamp(System.currentTimeMillis());
        message.rooms = rooms;
        return message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public Room getRooms() {
        return rooms;
    }

    public void setRooms(Room rooms) {
        this.rooms = rooms;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return id == message.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
