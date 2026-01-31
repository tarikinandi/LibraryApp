package com.libraryapp.repository;

import com.libraryapp.model.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book , Long> {

    @Query("SELECT b FROM Book b WHERE b.publishDate > :date")
    List<Book> findBooksPublishedAfter(@Param("date") LocalDate date);
}
