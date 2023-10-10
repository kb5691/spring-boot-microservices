package hello.bookapi.listeners;

import java.util.List;
import java.util.Optional;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.khello.shared.domain.AuthorEventDto;
import com.khello.shared.models.CustomMessage;

import hello.bookapi.config.RabbitMQConfig;
import hello.bookapi.models.Author;
import hello.bookapi.models.Book;
import hello.bookapi.repositories.AuthorRepository;
import hello.bookapi.repositories.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AuthorDeletedEventListener {
  private final AuthorRepository authorRepository;
  private final BookRepository bookRepository;

  @RabbitListener(queues = { RabbitMQConfig.QUEUE_AUTHOR_DELETED })
  public void handleMessage(CustomMessage<AuthorEventDto> message) {
    log.info("{} got triggered. got a message: {}", AuthorDeletedEventListener.class, message.toString());
    AuthorEventDto authorEventDto = message.getPayload();
    Optional<Author> maybeExistingAuthor = authorRepository.findById(authorEventDto.getId());
    if (maybeExistingAuthor.isEmpty()) {
      return;
    }
    authorRepository.delete(maybeExistingAuthor.get());

    List<Book> books = bookRepository.findAllByAuthors(authorEventDto.getId());
    for(Book bookItem: books) {
      bookItem.getAuthors().remove(authorEventDto.getId());
    }
  }
}
