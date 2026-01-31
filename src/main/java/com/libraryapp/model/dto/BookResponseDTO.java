package com.libraryapp.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BookResponseDTO(
        String title,
        Double price,
        @JsonProperty("ISBN13")
        String isbn13,
        String publisherName,
        String authorNameSurname
) {}