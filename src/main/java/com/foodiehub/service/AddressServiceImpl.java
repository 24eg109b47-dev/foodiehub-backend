package com.foodiehub.service;

import com.foodiehub.dto.AddressDto;
import com.foodiehub.entity.Address;
import com.foodiehub.entity.User;
import com.foodiehub.exception.ResourceNotFoundException;
import com.foodiehub.exception.UnauthorizedAccessException;
import com.foodiehub.repository.AddressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserService userService;

    public AddressServiceImpl(AddressRepository addressRepository, UserService userService) {
        this.addressRepository = addressRepository;
        this.userService = userService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Address> getAddressesForUser(Long userId) {
        User user = userService.getUserById(userId);
        return addressRepository.findByUser(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Address getAddressById(Long addressId, Long userId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));
        if (!address.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("You are not authorized to access this address.");
        }
        return address;
    }

    @Override
    public Address addAddress(Long userId, AddressDto dto) {
        User user = userService.getUserById(userId);
        List<Address> existing = addressRepository.findByUser(user);
        
        Address address = new Address();
        address.setUser(user);
        address.setLabel(dto.getLabel());
        address.setAddressLine(dto.getAddressLine());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setPincode(dto.getPincode());
        address.setPhone(dto.getPhone());
        
        // If first address or requested default, set as default
        boolean makeDefault = dto.isDefaultAddress() || existing.isEmpty();
        address.setDefaultAddress(makeDefault);

        if (makeDefault && !existing.isEmpty()) {
            clearPreviousDefaults(user);
        }

        return addressRepository.save(address);
    }

    @Override
    public Address updateAddress(Long userId, Long addressId, AddressDto dto) {
        Address address = getAddressById(addressId, userId);
        address.setLabel(dto.getLabel());
        address.setAddressLine(dto.getAddressLine());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setPincode(dto.getPincode());
        address.setPhone(dto.getPhone());

        if (dto.isDefaultAddress() && !address.isDefaultAddress()) {
            clearPreviousDefaults(address.getUser());
            address.setDefaultAddress(true);
        }

        return addressRepository.save(address);
    }

    @Override
    public void deleteAddress(Long userId, Long addressId) {
        Address address = getAddressById(addressId, userId);
        boolean wasDefault = address.isDefaultAddress();
        addressRepository.delete(address);

        if (wasDefault) {
            List<Address> remaining = addressRepository.findByUser(address.getUser());
            if (!remaining.isEmpty()) {
                Address nextDefault = remaining.get(0);
                nextDefault.setDefaultAddress(true);
                addressRepository.save(nextDefault);
            }
        }
    }

    @Override
    public Address setDefaultAddress(Long userId, Long addressId) {
        Address address = getAddressById(addressId, userId);
        clearPreviousDefaults(address.getUser());
        address.setDefaultAddress(true);
        return addressRepository.save(address);
    }

    private void clearPreviousDefaults(User user) {
        List<Address> addresses = addressRepository.findByUser(user);
        for (Address addr : addresses) {
            if (addr.isDefaultAddress()) {
                addr.setDefaultAddress(false);
                addressRepository.save(addr);
            }
        }
    }
}
