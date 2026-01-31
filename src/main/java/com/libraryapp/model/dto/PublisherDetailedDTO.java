package com.libraryapp.model.dto;

import java.util.List;

public record PublisherDetailedDTO(
        Long publisherID,
        String publisherName,
        List<BookResponseDTO> books
) {
}
