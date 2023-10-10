package hello.bookapi.listeners;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

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
public class AuthorUpdatedEventListener  {
  public final AuthorRepository _authorRepository;
  private final BookRepository _bookRepository;

  @RabbitListener(queues = { RabbitMQConfig.QUEUE_AUTHOR_UPDATED })
  public void handleMessage(CustomMessage<AuthorEventDto> message) {
    log.info("{} got triggered. got a message: {}", AuthorUpdatedEventListener.class, message.toString());
    AuthorEventDto authorEventDto = message.getPayload();
    Optional<Author> maybeExistingAuthor = _authorRepository.findById(authorEventDto.getId());
    if (maybeExistingAuthor.isEmpty()) {
      return;
    }
    Author existingAuthor = maybeExistingAuthor.get();
    existingAuthor.setName(authorEventDto.getName());
    existingAuthor.setDescription(authorEventDto.getDescription());
    _authorRepository.save(existingAuthor);

    List<Book> oldBooks = _bookRepository.findAllByAuthors(authorEventDto.getId());
    List<UUID> newBookIds = authorEventDto.getBooks();
    List<Book> newBooks = _bookRepository.findAllById(newBookIds);

    for (Book newBookItem : newBooks) {
      if (!newBookItem.getAuthors().contains(authorEventDto.getId())) {
        newBookItem.getAuthors().add(authorEventDto.getId());
      }
    }

    for (Book oldBookItem : oldBooks) {
      if (!newBookIds.contains(oldBookItem.getId())) {
        oldBookItem.getAuthors().remove(authorEventDto.getId());
      }
    }
  }
}
