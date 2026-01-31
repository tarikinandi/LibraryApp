package com.libraryapp.service.impl;

import com.libraryapp.model.dto.AuthorResponseDTO;
import com.libraryapp.repository.AuthorRepository;
import com.libraryapp.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    @Override
    public List<AuthorResponseDTO> getAllAuthors() {
        return authorRepository.findAll().stream()
                .map(author -> new AuthorResponseDTO(
                        author.getAuthorID(),
                        author.getAuthorNameSurname(),
                        (author.getBook() != null) ? author.getBook().getTitle() : "No Book"
                ))
                .collect(Collectors.toList());
    }
}
