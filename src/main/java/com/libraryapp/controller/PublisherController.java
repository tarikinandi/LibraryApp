package com.libraryapp.controller;

import com.libraryapp.model.dto.PublisherDetailedDTO;
import com.libraryapp.model.dto.PublisherResponseDTO;
import com.libraryapp.service.PublisherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/publishers")
@RequiredArgsConstructor
@Tag(name = "Publisher Management", description = "Yayınevi Listeleme İşlemleri")
public class PublisherController {

    private final PublisherService publisherService;

    @GetMapping
    @Operation(summary = "Tüm Yayınevlerini Getir")
    public ResponseEntity<List<PublisherResponseDTO>> getAllPublishers() {
        return ResponseEntity.ok(publisherService.getAllPublishers());
    }

    @GetMapping("/top2-details")
    @Operation(summary = "Rapor: İlk 2 Yayınevi (Kitapları ve Yazarlarıyla Birlikte)")
    public ResponseEntity<List<PublisherDetailedDTO>> getTop2PublishersWithBooks() {
        return ResponseEntity.ok(publisherService.getTop2PublishersWithBooks());
    }
}
