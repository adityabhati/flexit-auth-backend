package com.flexit.user_management.dao.daoimpl;

import com.flexit.user_management.dao.AddressDao;
import com.flexit.user_management.model.Address;
import com.flexit.user_management.repository.AddressRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddressDaoImpl implements AddressDao {
    private final AddressRepo addressRepo;

    @Override
    public void save(Address address) {
        addressRepo.save(address);
    }
}