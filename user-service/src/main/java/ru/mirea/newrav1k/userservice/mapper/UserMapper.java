package ru.mirea.newrav1k.userservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.mirea.newrav1k.userservice.model.entity.User;
import ru.newrav1k.mirea.core.model.payload.UserResponse;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    @Mapping(target = "userId", source = "id")
    UserResponse toUserResponse(User user);

}