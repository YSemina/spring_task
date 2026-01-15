package y.semina.service;

public interface JsonConverter <E, REQ, RES> {

    REQ jsonToRequestDto(String json);

    E requestDtoToEntity(REQ requestDto);

    RES entityToResponseDto(E entity);

    String responseDtoToJson(RES responseDto);

}
