package hello.bookapi.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.khello.shared.constants.RabbitMQKeys;
import com.khello.shared.errors.NotFoundException;
import com.khello.shared.models.CustomMessage;

import hello.bookapi.dtos.AuthorDto;
import hello.bookapi.dtos.BookDto;
import hello.bookapi.dtos.CreateBookDto;
import hello.bookapi.dtos.UpdateBookDto;
import hello.bookapi.mappers.AuthorMapper;
import hello.bookapi.mappers.BookMapper;
import hello.bookapi.models.Author;
import hello.bookapi.models.Book;
import hello.bookapi.repositories.AuthorRepository;
import hello.bookapi.repositories.BookRepository;
import com.khello.shared.domain.BookEventDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
  private final BookRepository _bookRepository;
  private final AuthorRepository _authorRepository;
  private final BookMapper _bookMapper;
  private final AuthorMapper _authorMapper;
  private final RabbitTemplate _template;

  @Override
  public List<BookDto> getBooks() {
    return _bookRepository.findAll().stream().map(bookItem -> _bookMapper.toDto(bookItem)).toList();
  }

  @Override
  public BookDto getBook(UUID id) {
    Book existingBook = _findBookById(id);
    List<Author> exsistingAuthors = _authorRepository.findAllById(existingBook.getAuthors());
    List<AuthorDto> authorDtos = exsistingAuthors.stream().map(authorItem -> _authorMapper.toDto(authorItem)).toList();
    BookDto bookDto = _bookMapper.toDto(existingBook);
    bookDto.setAuthors(authorDtos);
    return bookDto;
  }

  @Override
  public BookDto createBook(CreateBookDto dto) {
    Book savedBook = _bookRepository.save(new Book(dto.getTitle(), dto.getDescription(), dto.getAuthors()));
    CustomMessage<BookEventDto> message = new CustomMessage<>();
    message.setMessageId(UUID.randomUUID());
    message.setMessageDate(LocalDateTime.now());
    message.setPayload(_bookMapper.toEventDto(savedBook));
    _template.convertAndSend(RabbitMQKeys.BOOK_CRATED_EXCHANGE, null, message);
    return _bookMapper.toDto(savedBook);
  }

  @Override
  public void deleteBook(UUID id) {
    Book book = _findBookById(id);
    _bookRepository.delete(book);
    CustomMessage<BookEventDto> message = new CustomMessage<>();
    message.setMessageId(UUID.randomUUID());
    message.setMessageDate(LocalDateTime.now());
    message.setPayload(_bookMapper.toEventDto(book));
    _template.convertAndSend(RabbitMQKeys.BOOK_DELETED_EXCHANGE, null, message);
  }

  @Override
  public void updateBook(UpdateBookDto dto, UUID id) {
    Book found = _findBookById(id);

    if (Objects.nonNull(dto.getTitle())) {
      found.setTitle(dto.getTitle());
    }

    if (Objects.nonNull(dto.getDescription())) {
      found.setDescription(dto.getDescription());
    }

    if (Objects.nonNull(dto.getAuthors())) {
      found.setAuthors(dto.getAuthors());
    }

    _bookRepository.save(found);

    CustomMessage<BookEventDto> message = new CustomMessage<>();
    message.setMessageId(UUID.randomUUID());
    message.setMessageDate(LocalDateTime.now());
    _template.convertAndSend(RabbitMQKeys.BOOK_UPDATED_EXCHANGE, null, message);
  }

  private Book _findBookById(UUID id) {
    Optional<Book> result = _bookRepository.findById(id);

    if (result.isEmpty()) {
      throw new NotFoundException("Not found with this ID: " + id);
    }

    return result.get();
  }

}
