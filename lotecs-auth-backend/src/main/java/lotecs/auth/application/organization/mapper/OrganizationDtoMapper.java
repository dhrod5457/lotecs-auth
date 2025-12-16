package lotecs.auth.application.organization.mapper;

import lotecs.auth.application.organization.dto.OrganizationDto;
import lotecs.auth.domain.organization.model.Organization;
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
public interface OrganizationDtoMapper {

    OrganizationDto toDto(Organization organization);

    List<OrganizationDto> toDtoList(List<Organization> organizations);

    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Organization toEntity(OrganizationDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "organizationId", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(OrganizationDto dto, @MappingTarget Organization organization);
}
