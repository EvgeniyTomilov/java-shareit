package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusOfBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@DataJpaTest
public class BookingRepositoryTest {

    private final TestEntityManager entityManager;
    private final BookingRepository bookingRepository;

    private final PageRequest pageRequest = PageRequest.of(0, 5);
    private User user;
    private Item item;
    private Booking booking1;
    private Booking booking2;
    private Booking booking3;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("email@email.com")
                .name("name")
                .build();
        item = Item.builder()
                .name("name")
                .description("desc")
                .isAvailable(true)
                .owner(user)
                .build();
        booking1 = Booking.builder()
                .start(LocalDateTime.now().plusHours(10))
                .end(LocalDateTime.now().plusHours(20))
                .item(item)
                .booker(user)
                .status(StatusOfBooking.WAITING)
                .build();
        booking2 = Booking.builder()
                .start(LocalDateTime.now().plusHours(5))
                .end(LocalDateTime.now().plusHours(20))
                .item(item)
                .booker(user)
                .status(StatusOfBooking.WAITING)
                .build();
        booking3 = Booking.builder()
                .start(LocalDateTime.now().plusHours(5))
                .end(LocalDateTime.now().plusHours(20))
                .item(item)
                .booker(user)
                .status(StatusOfBooking.WAITING)
                .build();
    }


    @Test
    void whenFindByBookerIdOrderByStartDesc() {
        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        List<Booking> expected = List.of(booking1, booking2);

        List<Booking> actual = bookingRepository.findByBookerIdOrderByStartDesc(user.getId(), pageRequest);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenFindAllByBookerIdAndStartAfterOrderByStartDesc() {
        booking1.setStart(LocalDateTime.now().plusDays(2));
        booking2.setStart(LocalDateTime.now().plusDays(1));
        booking3.setStart(LocalDateTime.now().minusDays(1));
        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        entityManager.persist(booking3);
        List<Booking> expected = List.of(booking1, booking2);

        List<Booking> actual = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(user.getId(),
                LocalDateTime.now(), pageRequest);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenFindAllByBookerIdAndEndBeforeOrderByStartDesc() {
        booking1.setEnd(LocalDateTime.now().minusDays(2));
        booking2.setEnd(LocalDateTime.now().minusDays(1));
        booking3.setEnd(LocalDateTime.now().plusDays(1));
        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        entityManager.persist(booking3);
        List<Booking> expected = List.of(booking1, booking2);

        List<Booking> actual = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(user.getId(),
                LocalDateTime.now());

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenFindAllByBookerIdAndEndBeforeOrderByStartDescWithPageRequest() {
        booking1.setEnd(LocalDateTime.now().minusDays(2));
        booking2.setEnd(LocalDateTime.now().minusDays(1));
        booking3.setEnd(LocalDateTime.now().plusDays(1));
        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        entityManager.persist(booking3);
        List<Booking> expected = List.of(booking1, booking2);

        List<Booking> actual = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(user.getId(),
                LocalDateTime.now(), pageRequest);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenFindAllByItemIdAndStartBeforeOrderByStart() {
        booking1.setStart(LocalDateTime.now().minusDays(2));
        booking2.setStart(LocalDateTime.now().minusDays(1));
        booking3.setStart(LocalDateTime.now().plusDays(1));
        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        entityManager.persist(booking3);
        List<Booking> expected = List.of(booking1, booking2);

        List<Booking> actual = bookingRepository.findAllByItemIdAndStartBeforeOrderByStart(item.getId(),
                LocalDateTime.now());

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenFindAllByItemIdAndStartAfterOrderByStartAsc() {
        booking1.setStart(LocalDateTime.now().plusDays(1));
        booking2.setStart(LocalDateTime.now().plusDays(2));
        booking3.setStart(LocalDateTime.now().minusDays(1));
        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        entityManager.persist(booking3);
        List<Booking> expected = List.of(booking1, booking2);

        List<Booking> actual = bookingRepository.findAllByItemIdAndStartAfterOrderByStartAsc(item.getId(),
                LocalDateTime.now());

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenFindAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc() {
        booking1.setStart(LocalDateTime.now().minusDays(1));
        booking2.setStart(LocalDateTime.now().minusDays(2));
        booking3.setStart(LocalDateTime.now().plusDays(1));
        booking1.setEnd(LocalDateTime.now().plusDays(3));
        booking2.setEnd(LocalDateTime.now().plusDays(2));
        booking3.setEnd(LocalDateTime.now().minusDays(1));
        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        entityManager.persist(booking3);
        List<Booking> expected = List.of(booking1, booking2);

        List<Booking> actual = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(user.getId(),
                LocalDateTime.now(), LocalDateTime.now().plusDays(1), pageRequest);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenFindAllByItemOwnerIdOrderByStartDesc() {
        booking1.setStart(LocalDateTime.now().plusDays(2));
        booking2.setStart(LocalDateTime.now().plusDays(1));
        booking3.setStart(LocalDateTime.now().minusDays(1));
        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        entityManager.persist(booking3);
        List<Booking> expected = List.of(booking1, booking2, booking3);

        List<Booking> actual = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(user.getId(),
                pageRequest);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenFindAllByItemOwnerIdAndStartAfterOrderByStartDesc() {
        booking1.setStart(LocalDateTime.now().plusDays(2));
        booking2.setStart(LocalDateTime.now().plusDays(1));
        booking3.setStart(LocalDateTime.now().minusDays(1));
        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        entityManager.persist(booking3);
        List<Booking> expected = List.of(booking1, booking2);

        List<Booking> actual = bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(user.getId(),
                LocalDateTime.now(), pageRequest);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenFindAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc() {
        booking1.setStart(LocalDateTime.now().minusDays(1));
        booking2.setStart(LocalDateTime.now().minusDays(2));
        booking3.setStart(LocalDateTime.now().plusDays(1));
        booking1.setEnd(LocalDateTime.now().plusDays(2));
        booking2.setEnd(LocalDateTime.now().plusDays(3));
        booking3.setEnd(LocalDateTime.now().minusDays(1));
        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        entityManager.persist(booking3);
        List<Booking> expected = List.of(booking1, booking2);

        List<Booking> actual = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(user.getId(),
                LocalDateTime.now(), LocalDateTime.now().plusDays(1), pageRequest);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenFindAllByItemOwnerIdAndEndBeforeOrderByStartDesc() {
        booking1.setEnd(LocalDateTime.now().minusDays(2));
        booking2.setEnd(LocalDateTime.now().minusDays(1));
        booking3.setEnd(LocalDateTime.now().plusDays(1));
        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        entityManager.persist(booking3);
        List<Booking> expected = List.of(booking1, booking2);

        List<Booking> actual = bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(user.getId(),
                LocalDateTime.now(), pageRequest);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenFindAllByItemOwnerIdAndStatusOrderByStartDesc() {
        booking1.setStart(LocalDateTime.now().plusDays(2));
        booking2.setStart(LocalDateTime.now().plusDays(1));
        booking3.setStart(LocalDateTime.now().plusDays(1));
        booking3.setStatus(StatusOfBooking.APPROVED);
        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        entityManager.persist(booking3);
        List<Booking> expected = List.of(booking1, booking2);

        List<Booking> actual = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(user.getId(),
                pageRequest, StatusOfBooking.WAITING);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenFindAllByBookerIdAndStatusOrderByStartDesc() {
        booking1.setStart(LocalDateTime.now().plusDays(2));
        booking2.setStart(LocalDateTime.now().plusDays(1));
        booking3.setStart(LocalDateTime.now().plusDays(1));
        booking3.setStatus(StatusOfBooking.APPROVED);
        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        entityManager.persist(booking3);
        List<Booking> expected = List.of(booking1, booking2);

        List<Booking> actual = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(user.getId(),
                pageRequest, StatusOfBooking.WAITING);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

}
