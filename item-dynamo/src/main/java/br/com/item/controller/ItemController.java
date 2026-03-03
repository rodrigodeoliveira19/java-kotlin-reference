package br.com.item.controller;

import br.com.item.dto.ItemRequest;
import br.com.item.dto.ItemResponse;
import br.com.item.service.ItemService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public ResponseEntity<List<ItemResponse>> listAll() {
        return ResponseEntity.ok(itemService.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(itemService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ItemResponse> create(@RequestBody @Valid ItemRequest request, HttpServletRequest servletRequest) {
        ItemResponse created = itemService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemResponse> update(@PathVariable String id, @RequestBody @Valid ItemRequest request) {
        ItemResponse updated = itemService.update(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        itemService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // business action: deactivate
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<ItemResponse> deactivate(@PathVariable String id) {
        ItemResponse r = itemService.deactivate(id);
        return ResponseEntity.ok(r);
    }
}

