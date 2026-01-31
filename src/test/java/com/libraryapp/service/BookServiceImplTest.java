package com.libraryapp.service;

import com.libraryapp.model.dto.BookRequestDTO;
import com.libraryapp.model.dto.BookResponseDTO;
import com.libraryapp.model.entity.Book;
import com.libraryapp.model.entity.Publisher;
import com.libraryapp.repository.AuthorRepository;
import com.libraryapp.repository.BookRepository;
import com.libraryapp.repository.PublisherRepository;
import com.libraryapp.service.impl.BookServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock private BookRepository bookRepository;
    @Mock private PublisherRepository publisherRepository;
    @Mock private AuthorRepository authorRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    void shouldSaveBookSuccessfully() {
        BookRequestDTO request = new BookRequestDTO("Clean Code", 50.0, "12345", LocalDate.now(), "Pearson", "Robert C. Martin");

        Publisher mockPublisher = Publisher.builder().publisherID(1L).publisherName("Pearson").build();
        Book mockBook = Book.builder().bookID(1L).title("Clean Code").publisher(mockPublisher).build();

        when(publisherRepository.findByPublisherName("Pearson")).thenReturn(Optional.of(mockPublisher));
        when(bookRepository.save(any(Book.class))).thenReturn(mockBook);

        BookResponseDTO response = bookService.saveBook(request);

        assertNotNull(response);
        assertEquals("Clean Code", response.title());
        verify(bookRepository).save(any(Book.class)); // Save metodunun çağrıldığını doğrula
    }

    @Test
    void shouldFilterBooksStartingWithA() {
        Publisher p = Publisher.builder().publisherName("Test Pub").build();
        Book b1 = Book.builder().title("Algoritma").publisher(p).build();
        Book b2 = Book.builder().title("Java").publisher(p).build();

        when(bookRepository.findAll()).thenReturn(List.of(b1, b2));

        List<BookResponseDTO> result = bookService.getBooksStartingWithA();

        assertEquals(1, result.size());
        assertEquals("Algoritma", result.get(0).title());
    }
}