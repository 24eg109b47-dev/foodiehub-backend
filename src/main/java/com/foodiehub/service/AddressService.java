package com.foodiehub.service;

import com.foodiehub.dto.AddressDto;
import com.foodiehub.entity.Address;

import java.util.List;

public interface AddressService {
    List<Address> getAddressesForUser(Long userId);
    Address getAddressById(Long addressId, Long userId);
    Address addAddress(Long userId, AddressDto dto);
    Address updateAddress(Long userId, Long addressId, AddressDto dto);
    void deleteAddress(Long userId, Long addressId);
    Address setDefaultAddress(Long userId, Long addressId);
}
