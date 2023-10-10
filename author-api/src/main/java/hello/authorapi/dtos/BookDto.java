package hello.authorapi.dtos;

import com.khello.shared.models.DtoBase;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class BookDto extends DtoBase {
  private String title;
  private String description;
}
