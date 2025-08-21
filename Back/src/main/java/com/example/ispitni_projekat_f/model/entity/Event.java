package com.example.ispitni_projekat_f.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime startsAt;
    @Column(nullable = false)
    private String location;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private RegisteredUser author;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "event_tag",
            joinColumns = @JoinColumn(name = "event_id", nullable = false),
            uniqueConstraints = @UniqueConstraint(columnNames = {"event_id", "tag"})
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Column(name = "tag", nullable = false, length = 128)
    private Set<String> tags;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Category category;
    private int maxOccupants;

    public Event(long id) {
        this.id = id;
    }
}
