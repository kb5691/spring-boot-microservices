package hello.authorapi.services;

import java.util.List;
import java.util.UUID;

import hello.authorapi.dtos.AuthorDto;
import hello.authorapi.dtos.CreateAuthorDto;
import hello.authorapi.dtos.UpdateAuthorDto;

public interface AuthorService {
  public List<AuthorDto> getAuthors();

  public AuthorDto getAuthor(UUID id);

  public AuthorDto createAuthor(CreateAuthorDto dto);

  public void deleteAuthor(UUID id);

  public void updateAuthor(UpdateAuthorDto dto, UUID id);
}
