package com.lucascosta.petapi.mapper;

import com.lucascosta.petapi.domain.pet.Address;
import com.lucascosta.petapi.dto.request.AddressRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    Address toAddress(AddressRequest request);
}
