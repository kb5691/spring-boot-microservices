package hello.authorapi.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import hello.authorapi.models.Book;

public interface BookRepository extends JpaRepository<Book, UUID> {

}
