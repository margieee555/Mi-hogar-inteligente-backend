package com.hogar360.household.application.dto;

import java.util.List;

public record AddRoomsCommand(Long householdId, String ownerEmail, List<String> rooms) {
}