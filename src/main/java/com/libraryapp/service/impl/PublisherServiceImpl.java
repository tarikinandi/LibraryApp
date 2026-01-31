package com.libraryapp.service.impl;

import com.libraryapp.model.dto.BookResponseDTO;
import com.libraryapp.model.dto.PublisherDetailedDTO;
import com.libraryapp.model.dto.PublisherResponseDTO;
import com.libraryapp.model.entity.Publisher;
import com.libraryapp.repository.PublisherRepository;
import com.libraryapp.service.PublisherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublisherServiceImpl implements PublisherService {

    private final PublisherRepository publisherRepository;

    @Override
    public List<PublisherResponseDTO> getAllPublishers() {
        return publisherRepository.findAll().stream()
                .map(p -> new PublisherResponseDTO(p.getPublisherID(), p.getPublisherName()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PublisherDetailedDTO> getTop2PublishersWithBooks() {
        return publisherRepository.findTop2ByOrderByPublisherID().stream()
                .map(this::mapToDetailedDTO)
                .collect(Collectors.toList());
    }

    private PublisherDetailedDTO mapToDetailedDTO(Publisher publisher) {
        List<BookResponseDTO> bookDTOs = publisher.getBooks().stream()
                .map(book -> {
                    String authorName = (book.getAuthor() != null)
                            ? book.getAuthor().getAuthorNameSurname()
                            : "Unknown";

                    return new BookResponseDTO(
                            book.getTitle(),
                            book.getPrice(),
                            book.getIsbn13(),
                            book.getPublisher().getPublisherName(),
                            authorName
                    );
                }).collect(Collectors.toList());

        return new PublisherDetailedDTO(
                publisher.getPublisherID(),
                publisher.getPublisherName(),
                bookDTOs
        );
    }
}