package com.Marketplace_Management.Delivery.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.Marketplace_Management.Delivery.Contracts.IAddressRepository;
import com.Marketplace_Management.Delivery.DTOs.Commands.Address.SaveAddressCommand;
import com.Marketplace_Management.Delivery.Models.Address;
import com.Marketplace_Management.Delivery.Services.AddressService;
import com.Marketplace_Management.Shared.Errors.Exceptions.BadRequestException;
import com.Marketplace_Management.Shared.Errors.Exceptions.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock IAddressRepository repo;

    @InjectMocks AddressService service;

    private final UUID userId = UUID.randomUUID();

    private SaveAddressCommand command(Boolean isDefault) {
        return SaveAddressCommand.builder().wardId("00004").detail("12 Nguyen Trai").isDefault(isDefault).build();
    }

    private Address address(long id, boolean isDefault) {
        return Address.builder().id(id).userId(userId).wardId("00004").detail("x").isDefault(isDefault).build();
    }

    @Test
    void firstAddress_becomesDefault_evenIfNotRequested() {
        when(repo.wardExists("00004")).thenReturn(true);
        when(repo.findDefault(userId)).thenReturn(Optional.empty());
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.createAddress(userId, command(false));

        ArgumentCaptor<Address> saved = ArgumentCaptor.forClass(Address.class);
        verify(repo).save(saved.capture());
        assertTrue(saved.getValue().isDefaultAddress());
        assertEquals(userId, saved.getValue().getUserId());
    }

    @Test
    void newDefault_clearsThePreviousDefault() {
        when(repo.wardExists("00004")).thenReturn(true);
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.createAddress(userId, command(true));

        verify(repo).clearDefault(userId);
    }

    @Test
    void unknownWard_isRejected() {
        when(repo.wardExists("00004")).thenReturn(false);

        assertThrows(BadRequestException.class, () -> service.createAddress(userId, command(null)));
        verify(repo, never()).save(any());
    }

    @Test
    void anotherUsersAddress_isNotFound() {
        when(repo.findByIdAndUser(7L, userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.updateAddress(userId, 7L, command(null)));
        assertThrows(ResourceNotFoundException.class, () -> service.deleteAddress(userId, 7L));
        verify(repo, never()).save(any());
        verify(repo, never()).delete(any());
    }

    @Test
    void defaultAddress_cannotBeUnsetByUpdate() {
        Address current = address(7L, true);
        when(repo.findByIdAndUser(7L, userId)).thenReturn(Optional.of(current));
        when(repo.wardExists("00004")).thenReturn(true);
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.updateAddress(userId, 7L, command(false));

        assertTrue(current.isDefaultAddress());
        verify(repo, never()).clearDefault(any());
    }

    @Test
    void deletingTheDefault_promotesTheLatestRemainingAddress() {
        Address next = address(8L, false);
        when(repo.findByIdAndUser(7L, userId)).thenReturn(Optional.of(address(7L, true)));
        when(repo.findLatest(userId)).thenReturn(Optional.of(next));

        service.deleteAddress(userId, 7L);

        verify(repo).delete(7L);
        assertTrue(next.isDefaultAddress());
        verify(repo).save(next);
    }
}
