package hello.bookapi.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import hello.bookapi.models.Book;

public interface BookRepository extends JpaRepository<Book, UUID> {
  List<Book> findAllByAuthors(UUID authorId);
}
