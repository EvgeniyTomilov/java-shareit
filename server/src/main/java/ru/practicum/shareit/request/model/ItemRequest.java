package ru.practicum.shareit.request.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "requests", schema = "public")
@EqualsAndHashCode
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    private Long id;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "requester_id")
    @ToString.Exclude
    private User requester;

    @CreationTimestamp
    private LocalDateTime created;


    @Override
    public String toString() {
        return "ItemRequest{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", requester=" + requester +
                ", created=" + created +
                '}';
    }
}