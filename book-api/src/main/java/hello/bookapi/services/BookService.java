package hello.bookapi.services;

import java.util.List;
import java.util.UUID;

import hello.bookapi.dtos.BookDto;
import hello.bookapi.dtos.CreateBookDto;
import hello.bookapi.dtos.UpdateBookDto;

public interface BookService {
  public List<BookDto> getBooks();

  public BookDto getBook(UUID id);

  public BookDto createBook(CreateBookDto dto);

  public void deleteBook(UUID id);

  public void updateBook(UpdateBookDto dto, UUID id);
}
