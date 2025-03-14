package com.harryberlin.cvnest.mapper.register;

import com.harryberlin.cvnest.domain.User;
import com.harryberlin.cvnest.dto.request.RegisterRequest;
import com.harryberlin.cvnest.dto.response.RegisterResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RegisterMapper {
    User toEntity(RegisterRequest request);
    RegisterResponse toResponse(User user);
}
