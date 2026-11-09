package com.hogar360.inventory.infrastructure.adapter.in.web;

import com.hogar360.inventory.application.dto.CreateInventoryItemCommand;
import com.hogar360.inventory.application.dto.UpdateInventoryItemCommand;
import com.hogar360.inventory.domain.model.InventoryCategory;
import com.hogar360.inventory.domain.model.InventoryItem;
import com.hogar360.inventory.domain.port.in.ManageInventoryUseCase;
import com.hogar360.inventory.infrastructure.adapter.in.web.dto.ChangeQuantityRequest;
import com.hogar360.inventory.infrastructure.adapter.in.web.dto.InventoryItemRequest;
import com.hogar360.inventory.infrastructure.adapter.in.web.dto.InventoryItemResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Adaptador de entrada HTTP del inventario. Protegido por el JwtAuthenticationFilter:
 * authentication.getName() es el email del JWT.
 */
@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {

    private final ManageInventoryUseCase manageInventoryUseCase;

    public InventoryController(ManageInventoryUseCase manageInventoryUseCase) {
        this.manageInventoryUseCase = manageInventoryUseCase;
    }

    @GetMapping
    public ResponseEntity<List<InventoryItemResponse>> list(@RequestParam(required = false) InventoryCategory category,
                                                            Authentication authentication) {
        List<InventoryItemResponse> body = manageInventoryUseCase.list(authentication.getName(), category)
                .stream()
                .map(InventoryItemResponse::from)
                .toList();
        return ResponseEntity.ok(body);
    }

    @PostMapping
    public ResponseEntity<InventoryItemResponse> create(@Valid @RequestBody InventoryItemRequest request,
                                                        Authentication authentication) {
        InventoryItem item = manageInventoryUseCase.create(new CreateInventoryItemCommand(
                authentication.getName(), request.name(), request.category(), request.quantity(),
                request.unit(), request.targetQuantity(), request.expirationDate()));
        return ResponseEntity.status(HttpStatus.CREATED).body(InventoryItemResponse.from(item));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventoryItemResponse> update(@PathVariable Long id,
                                                        @Valid @RequestBody InventoryItemRequest request,
                                                        Authentication authentication) {
        InventoryItem item = manageInventoryUseCase.update(new UpdateInventoryItemCommand(
                id, authentication.getName(), request.name(), request.category(), request.quantity(),
                request.unit(), request.targetQuantity(), request.expirationDate()));
        return ResponseEntity.ok(InventoryItemResponse.from(item));
    }

    @PatchMapping("/{id}/quantity")
    public ResponseEntity<InventoryItemResponse> changeQuantity(@PathVariable Long id,
                                                                @Valid @RequestBody ChangeQuantityRequest request,
                                                                Authentication authentication) {
        InventoryItem item = manageInventoryUseCase.changeQuantity(id, authentication.getName(), request.quantity());
        return ResponseEntity.ok(InventoryItemResponse.from(item));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {
        manageInventoryUseCase.delete(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}