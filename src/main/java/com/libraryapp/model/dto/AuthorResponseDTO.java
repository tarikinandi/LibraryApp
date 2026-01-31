package com.libraryapp.model.dto;

public record AuthorResponseDTO(
        Long authorID,
        String nameSurname,
        String bookTitle
) {
}
