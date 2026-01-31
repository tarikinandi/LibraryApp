package com.libraryapp.controller;

import com.libraryapp.model.dto.BookRequestDTO;
import com.libraryapp.model.dto.BookResponseDTO;
import com.libraryapp.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
@Tag(name = "Book Management", description = "Kitap Ekleme, Listeleme ve Arama İşlemleri")
public class BookController {

    private final BookService bookService;

    @PostMapping
    @Operation(summary = "Yeni Kitap Kaydet", description = "Kitap verisiyle birlikte yazar ve yayınevi bilgisi de işlenir.")
    public ResponseEntity<BookResponseDTO> createBook(@Valid @RequestBody BookRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.saveBook(request));
    }

    @GetMapping
    @Operation(summary = "Tüm Kitapları Getir")
    public ResponseEntity<List<BookResponseDTO>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @GetMapping("/filter/starts-with-a")
    @Operation(summary = "Filtre: 'A' harfi ile başlayan kitaplar")
    public ResponseEntity<List<BookResponseDTO>> getBooksStartingWithA() {
        return ResponseEntity.ok(bookService.getBooksStartingWithA());
    }

    @GetMapping("/filter/after-2023")
    @Operation(summary = "Filtre: 2023 yılından sonra basılan kitaplar")
    public ResponseEntity<List<BookResponseDTO>> getBooksAfter2023() {
        return ResponseEntity.ok(bookService.getBooksAfter2023());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Kitap Güncelle", description = "ID'si verilen kitabı günceller. Yazar ve Yayınevi bilgisi de güncellenir.")
    public ResponseEntity<BookResponseDTO> updateBook(@PathVariable Long id, @Valid @RequestBody BookRequestDTO request) {
        return ResponseEntity.ok(bookService.updateBook(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Kitap Sil", description = "ID'si verilen kitabı ve bağlı yazar kaydını siler.")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build(); // 204 No Content döner
    }

    @GetMapping("/search/external")
    @Operation(summary = "Google Books API ile Kitap Ara")
    public ResponseEntity<BookResponseDTO> searchExternalBook(@RequestParam String query) {
        return ResponseEntity.ok(bookService.searchExternalBook(query));
    }
}
