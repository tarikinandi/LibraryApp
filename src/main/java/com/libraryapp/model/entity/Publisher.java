package com.libraryapp.model.entity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "publishers")
public class Publisher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long publisherID;

    @Column(unique = true)
    private String publisherName;

    @OneToMany(mappedBy = "publisher", cascade = CascadeType.ALL , fetch = FetchType.LAZY)
    private List<Book> books;
}
