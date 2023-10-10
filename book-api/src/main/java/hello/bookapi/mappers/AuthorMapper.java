package hello.bookapi.mappers;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.khello.shared.domain.AuthorEventDto;

import hello.bookapi.dtos.AuthorDto;
import hello.bookapi.models.Author;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthorMapper {
  private final ModelMapper modelMapper;

  public Author toEntity(AuthorEventDto eventDto) {
    return modelMapper.map(eventDto, Author.class);
  }

  public AuthorDto toDto(Author entity) {
    return modelMapper.map(entity, AuthorDto.class);
  }
}
