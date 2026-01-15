package y.semina.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
public class CustomerRequestDto {

    @NotBlank(message = "Поле first_name не может быть пустым!")
    @Size(min = 2, max = 50, message = "Имя должно содержать от 5 до 50 символов.")
    private String firstName;

    @NotBlank(message = "Поле last_name не может быть пустым!")
    @Size(min = 2, max = 50, message = "Фамилия должна содержать от 5 до 50 символов.")
    private String lastName;

    @NotBlank(message = "Поле email не может быть пустым!")
    @Email(message = "Введенный email некорректный.")
    private String email;

    @NotBlank(message = "Поле contact_number не может быть пустым!")
    @Pattern(regexp = "\\+?[0-9]{10,15}", message = "Контактный номер телефона должен состоять из 10-15 цифр.")
    private String contactNumber;

}
