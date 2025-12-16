package lotecs.auth.application.user.mapper;

import lotecs.auth.application.user.dto.CreateUserRequest;
import lotecs.auth.application.user.dto.UserDto;
import lotecs.auth.domain.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserDtoMapper {

    @Mapping(target = "roles", expression = "java(user.getRoles().stream().map(role -> role.getRoleName()).collect(java.util.stream.Collectors.toList()))")
    UserDto toDto(User user);

    List<UserDto> toDtoList(List<User> users);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "accountNonLocked", constant = "true")
    @Mapping(target = "credentialsNonExpired", constant = "true")
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "failedLoginAttempts", constant = "0")
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    User toEntity(CreateUserRequest request);
}
