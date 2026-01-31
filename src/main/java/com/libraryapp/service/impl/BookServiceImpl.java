package com.libraryapp.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.libraryapp.client.GoogleBooksClient;
import com.libraryapp.exception.ResourceNotFoundException;
import com.libraryapp.model.dto.BookRequestDTO;
import com.libraryapp.model.dto.BookResponseDTO;
import com.libraryapp.model.entity.Author;
import com.libraryapp.model.entity.Book;
import com.libraryapp.model.entity.Publisher;
import com.libraryapp.repository.AuthorRepository;
import com.libraryapp.repository.BookRepository;
import com.libraryapp.repository.PublisherRepository;
import com.libraryapp.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final PublisherRepository publisherRepository;
    private final AuthorRepository authorRepository;
    private final GoogleBooksClient googleBooksClient;
    private final ObjectMapper objectMapper;

    @Value("${google.books.api-key}")
    private String apiKey;

    @Override
    @Transactional
    public BookResponseDTO saveBook(BookRequestDTO request) {
        log.info("Creating book: {}", request.title());

        Publisher publisher = publisherRepository.findByPublisherName(request.publisherName())
                .orElseGet(() -> publisherRepository.save(
                        Publisher.builder().publisherName(request.publisherName()).build()
                ));

        Book book = Book.builder()
                .title(request.title())
                .price(request.price())
                .isbn13(request.isbn13())
                .publishDate(request.publishDate() != null ? request.publishDate() : LocalDate.now())
                .publisher(publisher)
                .build();

        Book savedBook = bookRepository.save(book);

        Author author = Author.builder()
                .authorNameSurname(request.authorNameSurname())
                .book(savedBook)
                .build();
        authorRepository.save(author);

        savedBook.setAuthor(author);

        return mapToDTO(savedBook);
    }

    @Override
    public List<BookResponseDTO> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookResponseDTO> getBooksStartingWithA() {
        return bookRepository.findAll().stream()
                .filter(b -> b.getTitle() != null && b.getTitle().startsWith("A"))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookResponseDTO> getBooksAfter2023() {
        return bookRepository.findBooksPublishedAfter(LocalDate.of(2023, 12, 31)).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookResponseDTO updateBook(Long id, BookRequestDTO request) {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Güncellenecek kitap bulunamadı. ID: " + id));

        if (!existingBook.getPublisher().getPublisherName().equals(request.publisherName())) {
            Publisher newPublisher = publisherRepository.findByPublisherName(request.publisherName())
                    .orElseGet(() -> publisherRepository.save(
                            Publisher.builder().publisherName(request.publisherName()).build()
                    ));
            existingBook.setPublisher(newPublisher);
        }

        existingBook.setTitle(request.title());
        existingBook.setPrice(request.price());
        existingBook.setIsbn13(request.isbn13());
        if(request.publishDate() != null) {
            existingBook.setPublishDate(request.publishDate());
        }

        if (existingBook.getAuthor() != null) {
            existingBook.getAuthor().setAuthorNameSurname(request.authorNameSurname());
        } else {
            Author newAuthor = Author.builder()
                    .authorNameSurname(request.authorNameSurname())
                    .book(existingBook)
                    .build();
            existingBook.setAuthor(newAuthor);
        }

        Book updatedBook = bookRepository.save(existingBook);
        return mapToDTO(updatedBook);
    }

    @Override
    @Transactional
    public void deleteBook(Long id) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Silinecek kitap bulunamadı. ID: " + id));
        bookRepository.delete(book);
    }

    @Override
    public BookResponseDTO searchExternalBook(String bookName) {
        try {
            log.info("Google Books API aranıyor: {}", bookName);

            String jsonResponse = googleBooksClient.searchBookByQuery(bookName, apiKey);
            JsonNode root = objectMapper.readTree(jsonResponse);

            JsonNode items = root.path("items");
            if (items.isEmpty()) {
                throw new ResourceNotFoundException("Google Books'ta kitap bulunamadı: " + bookName);
            }

            JsonNode item = items.get(0);
            JsonNode volumeInfo = item.path("volumeInfo");
            JsonNode saleInfo = item.path("saleInfo");


            String title = volumeInfo.path("title").asText("Bilinmeyen Başlık");

            String publisher = volumeInfo.path("publisher").asText("Bilinmeyen Yayınevi");

            String author = "Bilinmeyen Yazar";
            if (volumeInfo.has("authors")) {
                author = volumeInfo.path("authors").get(0).asText();
            }

            String isbn = "YOK";
            if (volumeInfo.has("industryIdentifiers")) {
                JsonNode identifiers = volumeInfo.path("industryIdentifiers");
                for (JsonNode idNode : identifiers) {
                    if ("ISBN_13".equals(idNode.path("type").asText())) {
                        isbn = idNode.path("identifier").asText();
                        break;
                    }
                }
                if (isbn.equals("YOK") && identifiers.size() > 0) {
                    isbn = identifiers.get(0).path("identifier").asText();
                }
            }

            Double price = 0.0;
            if (saleInfo.has("listPrice")) {
                price = saleInfo.path("listPrice").path("amount").asDouble();
            } else if (saleInfo.has("retailPrice")) {
                price = saleInfo.path("retailPrice").path("amount").asDouble();
            }

            return new BookResponseDTO(
                    title,
                    price,
                    isbn,
                    publisher,
                    author
            );

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Google API Parse Hatası: ", e);
            throw new RuntimeException("Dış servis hatası: " + e.getMessage());
        }
    }

    private BookResponseDTO mapToDTO(Book book) {
        String authorName = (book.getAuthor() != null) ? book.getAuthor().getAuthorNameSurname() : "Unknown";

        return new BookResponseDTO(
                book.getTitle(),
                book.getPrice(),
                book.getIsbn13(),
                book.getPublisher().getPublisherName(),
                authorName
        );
    }
}