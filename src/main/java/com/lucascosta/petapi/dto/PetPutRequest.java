package com.lucascosta.petapi.dto;

import java.time.LocalDate;

public record PetPutRequest (
        String name,
        AddressRequest address,
        Double weight
){
}
