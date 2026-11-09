package com.hogar360.statistics.domain.model;

public record InventoryOverview(int total, int lowStock, int expiringSoon, int expired) {
}