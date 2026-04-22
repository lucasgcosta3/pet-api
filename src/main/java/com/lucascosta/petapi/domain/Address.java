package com.lucascosta.petapi.domain;

import jakarta.persistence.*;
import lombok.*;

@Embeddable
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Address {

    @Column(nullable = false, name = "address_city")
    private String city;

    @Column(nullable = false, name = "address_street")
    private String street;

    @Column(name = "address_number")
    private String number;
}
