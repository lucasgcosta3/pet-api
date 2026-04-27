package com.lucascosta.petapi.mapper;

import com.lucascosta.petapi.domain.Address;
import com.lucascosta.petapi.dto.resquest.AddressRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    Address toAddress(AddressRequest request);
}
