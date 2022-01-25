package ru.job4j.repository;

import org.springframework.data.repository.CrudRepository;
import ru.job4j.model.Message;

public interface MessageRepository extends CrudRepository<Message, Integer> {
}
