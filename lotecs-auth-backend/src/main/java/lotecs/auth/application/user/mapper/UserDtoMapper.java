package lotecs.auth.application.user.mapper;

import lotecs.auth.application.user.dto.CreateUserRequest;
import lotecs.auth.application.user.dto.UserDto;
import lotecs.auth.domain.user.model.User;
import lotecs.auth.domain.user.model.UserStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
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
    @Mapping(target = "status", source = "status", qualifiedByName = "statusToString")
    UserDto toDto(User user);

    @Named("statusToString")
    default String statusToString(UserStatus status) {
        return status != null ? status.name() : null;
    }

    List<UserDto> toDtoList(List<User> users);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "status", expression = "java(lotecs.auth.domain.user.model.UserStatus.ACTIVE)")
    @Mapping(target = "accountNonLocked", constant = "true")
    @Mapping(target = "credentialsNonExpired", constant = "true")
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "failedLoginAttempts", constant = "0")
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    User toEntity(CreateUserRequest request);
}
