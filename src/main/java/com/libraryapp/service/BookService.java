package com.libraryapp.service;

import com.libraryapp.model.dto.BookRequestDTO;
import com.libraryapp.model.dto.BookResponseDTO;
import com.libraryapp.model.entity.Book;

import java.util.List;

public interface BookService {

    BookResponseDTO saveBook(BookRequestDTO request);
    List<BookResponseDTO> getAllBooks();
    List<BookResponseDTO> getBooksStartingWithA(); // Stream ile
    List<BookResponseDTO> getBooksAfter2023();     // JPA Query ile
    BookResponseDTO searchExternalBook(String bookName);
    BookResponseDTO updateBook(Long id, BookRequestDTO request);
    void deleteBook(Long id);
}
