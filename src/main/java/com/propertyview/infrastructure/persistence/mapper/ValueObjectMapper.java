package com.propertyview.infrastructure.persistence.mapper;

import com.propertyview.domain.model.*;
import com.propertyview.infrastructure.persistence.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;


/**
 * MapStruct mapper for converting value objects to their JPA embeddable counterparts
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ValueObjectMapper {
    Address toDomain(AddressEmbeddable source);
    AddressEmbeddable toEmbeddable(Address source);

    Contacts toDomain(ContactsEmbeddable source);
    ContactsEmbeddable toEmbeddable(Contacts source);

    ArrivalTime toDomain(ArrivalTimeEmbeddable source);
    ArrivalTimeEmbeddable toEmbeddable(ArrivalTime source);
}
