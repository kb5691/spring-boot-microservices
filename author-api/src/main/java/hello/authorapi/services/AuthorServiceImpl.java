package hello.authorapi.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.khello.shared.constants.RabbitMQKeys;
import com.khello.shared.domain.AuthorEventDto;
import com.khello.shared.errors.NotFoundException;
import com.khello.shared.models.CustomMessage;

import hello.authorapi.dtos.AuthorDto;
import hello.authorapi.dtos.CreateAuthorDto;
import hello.authorapi.dtos.UpdateAuthorDto;
import hello.authorapi.mappers.AuthorMapper;
import hello.authorapi.models.Author;
import hello.authorapi.repositories.AuthorRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {
  private final AuthorRepository _authorRepository;
  private final AuthorMapper _authorMapper;
  private final RabbitTemplate _template;

  @Override
  public List<AuthorDto> getAuthors() {
    return _authorRepository.findAll().stream().map(author -> _authorMapper.toDto(author)).toList();
  }

  @Override
  public AuthorDto getAuthor(UUID id) {
    return _authorMapper.toDto(_findAuthorById(id));
  }

  @Override
  public AuthorDto createAuthor(CreateAuthorDto dto) {
    Author savedAuthor = _authorRepository.save(new Author(dto.getName(), dto.getDescription(), dto.getBooks()));
    CustomMessage<AuthorEventDto> message = new CustomMessage<>();
    message.setMessageId(UUID.randomUUID());
    message.setMessageDate(LocalDateTime.now());
    message.setPayload(_authorMapper.toEventDto(savedAuthor));
    _template.convertAndSend(RabbitMQKeys.AUTHOR_CREATED_EXCHANGE, null, message);
    return _authorMapper.toDto(savedAuthor);
  }

  @Override
  public void deleteAuthor(UUID id) {
    Author author = _findAuthorById(id);
    _authorRepository.delete(author);
    CustomMessage<AuthorEventDto> message = new CustomMessage<>();
    message.setMessageDate(LocalDateTime.now());
    message.setMessageId(UUID.randomUUID());
    message.setPayload(_authorMapper.toEventDto(author));
    _template.convertAndSend(RabbitMQKeys.AUTHOR_DELETED_EXCHANGE, null, message);
  }

  @Override
  public void updateAuthor(UpdateAuthorDto dto, UUID id) {
    Author found = _findAuthorById(id);

    if (Objects.nonNull(dto.getName())) {
      found.setName(dto.getName());
    }

    if (Objects.nonNull(dto.getDescription())) {
      found.setDescription(dto.getDescription());
    }

    _authorRepository.save(found);
    CustomMessage<AuthorEventDto> message = new CustomMessage<>();
    message.setMessageDate(LocalDateTime.now());
    message.setMessageId(UUID.randomUUID());
    message.setPayload(_authorMapper.toEventDto(found));
    _template.convertAndSend(RabbitMQKeys.AUTHOR_UPDATED_EXCHANGE, null, message);
  }

  private Author _findAuthorById(UUID id) {
    Optional<Author> result = _authorRepository.findById(id);

    if (result.isEmpty()) {
      throw new NotFoundException("Not found with this ID: " + id);
    }

    return result.get();
  }

}
