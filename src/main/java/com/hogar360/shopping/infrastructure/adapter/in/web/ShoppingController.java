package com.hogar360.shopping.infrastructure.adapter.in.web;

import com.hogar360.shopping.application.dto.CreateShoppingItemCommand;
import com.hogar360.shopping.domain.model.ShoppingItem;
import com.hogar360.shopping.domain.port.in.ManageShoppingUseCase;
import com.hogar360.shopping.infrastructure.adapter.in.web.dto.ChangeShoppingQuantityRequest;
import com.hogar360.shopping.infrastructure.adapter.in.web.dto.ClearedResponse;
import com.hogar360.shopping.infrastructure.adapter.in.web.dto.SetBoughtRequest;
import com.hogar360.shopping.infrastructure.adapter.in.web.dto.ShoppingItemRequest;
import com.hogar360.shopping.infrastructure.adapter.in.web.dto.ShoppingItemResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Adaptador de entrada HTTP de la lista de compras. Protegido por el JwtAuthenticationFilter:
 * authentication.getName() es el email del JWT.
 */
@RestController
@RequestMapping("/api/v1/shopping")
public class ShoppingController {

    private final ManageShoppingUseCase manageShoppingUseCase;

    public ShoppingController(ManageShoppingUseCase manageShoppingUseCase) {
        this.manageShoppingUseCase = manageShoppingUseCase;
    }

    @GetMapping
    public ResponseEntity<List<ShoppingItemResponse>> list(Authentication authentication) {
        List<ShoppingItemResponse> body = manageShoppingUseCase.list(authentication.getName())
                .stream()
                .map(ShoppingItemResponse::from)
                .toList();
        return ResponseEntity.ok(body);
    }

    @PostMapping
    public ResponseEntity<ShoppingItemResponse> create(@Valid @RequestBody ShoppingItemRequest request,
                                                       Authentication authentication) {
        ShoppingItem item = manageShoppingUseCase.create(new CreateShoppingItemCommand(
                authentication.getName(), request.name(), request.category(), request.quantity()));
        return ResponseEntity.status(HttpStatus.CREATED).body(ShoppingItemResponse.from(item));
    }

    @PatchMapping("/{id}/quantity")
    public ResponseEntity<ShoppingItemResponse> changeQuantity(@PathVariable Long id,
                                                               @Valid @RequestBody ChangeShoppingQuantityRequest request,
                                                               Authentication authentication) {
        ShoppingItem item = manageShoppingUseCase.changeQuantity(id, authentication.getName(), request.quantity());
        return ResponseEntity.ok(ShoppingItemResponse.from(item));
    }

    @PatchMapping("/{id}/bought")
    public ResponseEntity<ShoppingItemResponse> setBought(@PathVariable Long id,
                                                          @Valid @RequestBody SetBoughtRequest request,
                                                          Authentication authentication) {
        ShoppingItem item = manageShoppingUseCase.setBought(id, authentication.getName(), request.bought());
        return ResponseEntity.ok(ShoppingItemResponse.from(item));
    }

    @DeleteMapping("/bought")
    public ResponseEntity<ClearedResponse> clearBought(Authentication authentication) {
        return ResponseEntity.ok(new ClearedResponse(manageShoppingUseCase.clearBought(authentication.getName())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {
        manageShoppingUseCase.delete(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
