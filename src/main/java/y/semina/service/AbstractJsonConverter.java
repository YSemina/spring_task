package y.semina.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class AbstractJsonConverter <E, REQ, RES> implements JsonConverter<E, REQ, RES> {

    protected final ObjectMapper objectMapper;
    protected final Validator validator;

    @Override
    public String responseDtoToJson(RES responseDto) {
        try {
            return objectMapper.writeValueAsString(responseDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected void validateRequestDto(REQ requestDto) throws ValidationException {
        Set<ConstraintViolation<REQ>> violations = validator.validate(requestDto);
        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                    .map(this::formatViolation)
                    .collect(Collectors.joining("; "));
            throw new ValidationException(errorMessage);
        }
    }

    private String formatViolation(ConstraintViolation<REQ> violation) {
        return String.format("%s: %s",
                violation.getPropertyPath(),
                violation.getMessage()
        );
    }

}
