package com.libraryapp.service;

import com.libraryapp.model.dto.PublisherDetailedDTO;
import com.libraryapp.model.dto.PublisherResponseDTO;

import java.util.List;

public interface PublisherService {
    List<PublisherResponseDTO> getAllPublishers();
    List<PublisherDetailedDTO> getTop2PublishersWithBooks();
}
