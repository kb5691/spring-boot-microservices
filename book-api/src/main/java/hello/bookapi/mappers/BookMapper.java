package hello.bookapi.mappers;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.khello.shared.domain.BookEventDto;

import hello.bookapi.dtos.BookDto;
import hello.bookapi.models.Book;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BookMapper {
  private final ModelMapper _modelMapper;

  public BookDto toDto(Book entity) {
    return _modelMapper.map(entity, BookDto.class);
  }

  public BookEventDto toEventDto(Book entity) {
    return _modelMapper.map(entity, BookEventDto.class);
  }
}
