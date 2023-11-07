package ru.practicum.shareit.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findByRequestor_IdOrderByCreatedDesc(long userId);

    List<ItemRequest> findByRequestor_IdIsNotOrderByCreatedDesc(long userId, PageRequest pageRequest);
}
