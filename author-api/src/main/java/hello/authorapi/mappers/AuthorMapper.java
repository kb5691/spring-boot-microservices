package hello.authorapi.mappers;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.khello.shared.domain.AuthorEventDto;

import hello.authorapi.dtos.AuthorDto;
import hello.authorapi.models.Author;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthorMapper {
  private final ModelMapper _modelMapper;

  public AuthorDto toDto(Author entity) {
    return _modelMapper.map(entity, AuthorDto.class);
  }

  public AuthorEventDto toEventDto(Author entity) {
    return _modelMapper.map(entity, AuthorEventDto.class);
  }
}
