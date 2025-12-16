package lotecs.auth.application.organization.mapper;

import lotecs.auth.application.organization.dto.UserOrganizationDto;
import lotecs.auth.domain.organization.model.UserOrganization;
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
public interface UserOrganizationDtoMapper {

    UserOrganizationDto toDto(UserOrganization userOrganization);

    List<UserOrganizationDto> toDtoList(List<UserOrganization> userOrganizations);

    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UserOrganization toEntity(UserOrganizationDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "organizationId", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(UserOrganizationDto dto, @MappingTarget UserOrganization userOrganization);
}
