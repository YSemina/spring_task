package y.semina.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerResponseDto {

    private String firstName;
    private String lastName;
    private String email;
    private Long contactNumber;

}
