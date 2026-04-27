package com.lucascosta.petapi.dto.resquest;

public record PetPutRequest (
        String name,
        AddressRequest address,
        Double weight
){
}
