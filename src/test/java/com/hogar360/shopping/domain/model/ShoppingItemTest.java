package com.hogar360.shopping.domain.model;

import com.hogar360.shared.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ShoppingItemTest {

    private ShoppingItem nuevo() {
        return ShoppingItem.createNew(5L, "  Pan Integral ", ShoppingCategory.FOOD, 2);
    }

    @Test
    void createNew_empiezaSinComprarYNormalizaElNombre() {
        ShoppingItem item = nuevo();

        assertThat(item.getId()).isNull();
        assertThat(item.getHouseholdId()).isEqualTo(5L);
        assertThat(item.getName()).isEqualTo("Pan Integral");
        assertThat(item.getQuantity()).isEqualTo(2);
        assertThat(item.isBought()).isFalse();
        assertThat(item.getBoughtAt()).isNull();
        assertThat(item.getCreatedAt()).isNotNull();
    }

    @Test
    void createNew_rechazaNombreVacio() {
        assertThatThrownBy(() -> ShoppingItem.createNew(5L, "  ", ShoppingCategory.FOOD, 1))
                .isInstanceOf(DomainValidationException.class)
                .hasFieldOrPropertyWithValue("code", "error.shopping.name.required");
    }

    @Test
    void createNew_rechazaCategoriaNula() {
        assertThatThrownBy(() -> ShoppingItem.createNew(5L, "Pan", null, 1))
                .isInstanceOf(DomainValidationException.class)
                .hasFieldOrPropertyWithValue("code", "error.shopping.category.required");
    }

    @Test
    void createNew_rechazaCantidadFueraDeRango() {
        assertThatThrownBy(() -> ShoppingItem.createNew(5L, "Pan", ShoppingCategory.FOOD, 0))
                .isInstanceOf(DomainValidationException.class)
                .hasFieldOrPropertyWithValue("code", "error.shopping.quantity.range");
        assertThatThrownBy(() -> ShoppingItem.createNew(5L, "Pan", ShoppingCategory.FOOD, 1000))
                .isInstanceOf(DomainValidationException.class)
                .hasFieldOrPropertyWithValue("code", "error.shopping.quantity.range");
    }

    @Test
    void withQuantity_cambiaSoloLaCantidad() {
        ShoppingItem cambiado = nuevo().withQuantity(5);

        assertThat(cambiado.getQuantity()).isEqualTo(5);
        assertThat(cambiado.getName()).isEqualTo("Pan Integral");
    }

    @Test
    void withQuantity_rechazaCantidadInvalida() {
        ShoppingItem item = nuevo();

        assertThatThrownBy(() -> item.withQuantity(0))
                .isInstanceOf(DomainValidationException.class)
                .hasFieldOrPropertyWithValue("code", "error.shopping.quantity.range");
    }

    @Test
    void withBought_marcarGuardaLaFechaDeCompra() {
        ShoppingItem comprado = nuevo().withBought(true);

        assertThat(comprado.isBought()).isTrue();
        assertThat(comprado.getBoughtAt()).isNotNull();
    }

    @Test
    void withBought_desmarcarBorraLaFechaDeCompra() {
        ShoppingItem pendiente = nuevo().withBought(true).withBought(false);

        assertThat(pendiente.isBought()).isFalse();
        assertThat(pendiente.getBoughtAt()).isNull();
    }
        @Test
    void withRestockedQuantity_guardaLaCantidadSumadaAlInventario() {
        ShoppingItem item = nuevo().withBought(true).withRestockedQuantity(2);

        assertThat(item.getRestockedQuantity()).isEqualTo(2);
    }

    @Test
    void withBought_desmarcarBorraLaReposicion() {
        ShoppingItem item = nuevo().withBought(true).withRestockedQuantity(2).withBought(false);

        assertThat(item.getRestockedQuantity()).isNull();
    }
}
