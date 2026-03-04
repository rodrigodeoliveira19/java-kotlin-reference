package br.com.item.service;

import br.com.item.dto.ItemRequest;
import br.com.item.dto.ItemResponse;
import br.com.item.event.ItemSqsPublisher;
import br.com.item.exception.BusinessException;
import br.com.item.exception.ResourceNotFoundException;
import br.com.item.model.Item;
import br.com.item.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemSqsPublisher publisher;

    public ItemService(
            ItemRepository itemRepository,
            ItemSqsPublisher publisher
    ) {
        this.itemRepository = itemRepository;
        this.publisher = publisher;
    }

    // Create
    public ItemResponse create(ItemRequest request) {
        // Business rule example 1: name must be unique
        boolean existsWithSameName = itemRepository.findAll().stream()
                .anyMatch(i -> i.getName().equalsIgnoreCase(request.getName()));
        if (existsWithSameName) {
            throw new BusinessException("ITEM_NAME_CONFLICT", "An item with the same name already exists");
        }

        Item item = new Item();
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setActive(true); // business default
        item.setCreatedAt(Instant.now());

        Item saved = itemRepository.save(item);
        publisher.publish(saved); // envio para fila.
        return toResponse(saved);
    }

    // Read
    public ItemResponse findById(String id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ITEM_NOT_FOUND", "Item not found with id: " + id));
        return toResponse(item);
    }

    // Update
    public ItemResponse update(String id, ItemRequest request) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ITEM_NOT_FOUND", "Item not found with id: " + id));

        // Business rule example 2: cannot change name to empty
        if (request.getName() == null || request.getName().isBlank()) {
            throw new BusinessException("INVALID_NAME", "Name cannot be blank");
        }

        item.setName(request.getName());
        item.setDescription(request.getDescription());

        Item saved = itemRepository.save(item);
        return toResponse(saved);
    }

    // Delete
    public void delete(String id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ITEM_NOT_FOUND", "Item not found with id: " + id));

        // Business rule example 3: cannot delete active items
        if (item.isActive()) {
            throw new BusinessException("CANNOT_DELETE_ACTIVE", "Cannot delete an active item. Deactivate it first.");
        }

        itemRepository.deleteById(id);
    }

    // Extra business operation 4: deactivate
    public ItemResponse deactivate(String id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ITEM_NOT_FOUND", "Item not found with id: " + id));
        if (!item.isActive()) {
            throw new BusinessException("ALREADY_INACTIVE", "Item is already inactive");
        }
        item.setActive(false);
        Item saved = itemRepository.save(item);
        return toResponse(saved);
    }

    // List
    public List<ItemResponse> listAll() {
        return itemRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    private ItemResponse toResponse(Item item) {
        ItemResponse r = new ItemResponse();
        r.setId(item.getId());
        r.setName(item.getName());
        r.setDescription(item.getDescription());
        r.setActive(item.isActive());
        r.setCreatedAt(item.getCreatedAt());
        return r;
    }
}

