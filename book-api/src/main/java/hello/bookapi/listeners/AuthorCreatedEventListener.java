package hello.bookapi.listeners;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;


import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import hello.bookapi.config.RabbitMQConfig;
import hello.bookapi.mappers.AuthorMapper;
import hello.bookapi.models.Author;
import hello.bookapi.models.Book;
import hello.bookapi.repositories.AuthorRepository;
import hello.bookapi.repositories.BookRepository;
import com.khello.shared.domain.AuthorEventDto;
import com.khello.shared.models.CustomMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AuthorCreatedEventListener {
  private final AuthorRepository _authorRepository;
  private final BookRepository _bookRepository;
  private final AuthorMapper _authorMapper;

  @RabbitListener(queues = { RabbitMQConfig.QUEUE_AUTHOR_CREATED })
  public void handleMessage(CustomMessage<AuthorEventDto> message) {
    log.info("{} got triggered. got a message: {}", AuthorCreatedEventListener.class, message.getPayload().toString());
    AuthorEventDto authorEventDto = message.getPayload();
    Optional<Author> existingAuthor = _authorRepository.findById(authorEventDto.getId());
    if (existingAuthor.isPresent()) {
      return;
    }
    Author newAuthor = _authorMapper.toEntity(authorEventDto);
    _authorRepository.save(newAuthor);

    List<UUID> bookIds = authorEventDto.getBooks();
    if (Objects.isNull(bookIds) || bookIds.size() == 0) {
      return;
    }

    List<Book> books = _bookRepository.findAllById(bookIds);

    for (Book bookItem : books) {
      if (!bookItem.getAuthors().contains(authorEventDto.getId())) {
        bookItem.getAuthors().add(authorEventDto.getId());
      }
    }
  }
}
