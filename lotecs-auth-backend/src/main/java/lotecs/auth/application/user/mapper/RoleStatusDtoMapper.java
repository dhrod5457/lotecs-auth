package lotecs.auth.application.user.mapper;

import lotecs.auth.application.user.dto.RoleStatusDto;
import lotecs.auth.domain.user.model.RoleStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface RoleStatusDtoMapper {

    RoleStatusDto toDto(RoleStatus roleStatus);

    List<RoleStatusDto> toDtoList(List<RoleStatus> roleStatuses);

    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    RoleStatus toEntity(RoleStatusDto dto);

    @Mapping(target = "statusCode", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(RoleStatusDto dto, @MappingTarget RoleStatus roleStatus);
}
