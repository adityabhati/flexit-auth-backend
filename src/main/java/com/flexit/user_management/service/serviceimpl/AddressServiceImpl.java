package com.flexit.user_management.service.serviceimpl;

import com.flexit.user_management.dao.AddressDao;
import com.flexit.user_management.model.Address;
import com.flexit.user_management.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressDao addressDao;

    @Override
    public void saveAddress(Address address) {
        addressDao.save(address);
    }
}