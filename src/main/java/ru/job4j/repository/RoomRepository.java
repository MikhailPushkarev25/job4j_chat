package ru.job4j.repository;

import org.springframework.data.repository.CrudRepository;
import ru.job4j.model.Room;

public interface RoomRepository extends CrudRepository<Room, Integer> {

}
