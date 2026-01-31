package com.libraryapp.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record BookRequestDTO(
        @NotBlank(message = "Kitap adı boş olamaz")
        String title,
        @NotNull(message = "Fiyat zorunludur")
        Double price,
        @NotBlank
        @JsonProperty("ISBN13")
        String isbn13,
        LocalDate publishDate,
        @NotBlank
        String publisherName,
        @NotBlank
        String authorNameSurname
) {}
