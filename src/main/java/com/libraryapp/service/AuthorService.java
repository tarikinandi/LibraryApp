package com.libraryapp.service;

import com.libraryapp.model.dto.AuthorResponseDTO;

import java.util.List;

public interface AuthorService {
    List<AuthorResponseDTO> getAllAuthors();
}
