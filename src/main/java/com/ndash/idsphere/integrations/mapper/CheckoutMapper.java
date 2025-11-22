package com.ndash.idsphere.integrations.mapper;

import com.ndash.idsphere.integrations.domain.Checkout;
import com.ndash.idsphere.integrations.dto.CheckoutResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CheckoutMapper {

    @Mapping(source = "application.id", target = "applicationId")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "createdBy.id", target = "createdByUserId")
    CheckoutResponse toDto(Checkout checkout);
}
