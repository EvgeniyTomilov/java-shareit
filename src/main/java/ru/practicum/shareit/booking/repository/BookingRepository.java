package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusOfBooking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId, PageRequest of);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime startTime, PageRequest of);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime startTime);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime startTime, PageRequest of);

    List<Booking> findAllByItemIdAndStartBeforeOrderByStart(Long itemId, LocalDateTime startTime);

    List<Booking> findAllByItemIdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime startTime);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, LocalDateTime startTime, LocalDateTime endTime, PageRequest of);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId, PageRequest pageRequest);

    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime startTime, PageRequest pageRequest);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long ownerId, LocalDateTime startTime, LocalDateTime endTime, PageRequest pageRequest);

    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime now, PageRequest pageRequest);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, PageRequest pageRequest, StatusOfBooking valueOf);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, PageRequest pageRequest, StatusOfBooking valueOf);
}