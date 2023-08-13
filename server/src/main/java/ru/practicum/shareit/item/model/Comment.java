package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "comments", schema = "public")
@EqualsAndHashCode
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    private Long id;

    @Column(name = "text")
    private String text;

    @ManyToOne
    @JoinColumn(name = "item_id")
    @ToString.Exclude
    private Item item;

    @ManyToOne
    @JoinColumn(name = "author_id")
    @ToString.Exclude
    private User author;

    private LocalDateTime created;

}