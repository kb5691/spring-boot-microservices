package hello.authorapi.mappers;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import hello.authorapi.dtos.BookDto;
import hello.authorapi.models.Book;
import com.khello.shared.domain.BookEventDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BookMapper {
  private final ModelMapper _modelMapper;

  public BookDto toDto(Book book) {
    return _modelMapper.map(book, BookDto.class);
  }

  public Book toEntity(BookEventDto eventDto) {
    return _modelMapper.map(eventDto, Book.class);
  }
}
