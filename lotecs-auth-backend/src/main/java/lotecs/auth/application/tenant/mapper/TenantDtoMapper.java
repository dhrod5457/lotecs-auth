package lotecs.auth.application.tenant.mapper;

import lotecs.auth.application.tenant.dto.TenantDto;
import lotecs.auth.domain.tenant.model.SiteStatus;
import lotecs.auth.domain.tenant.model.Tenant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface TenantDtoMapper {

    @Mapping(target = "status", source = "status", qualifiedByName = "statusToString")
    TenantDto toDto(Tenant tenant);

    List<TenantDto> toDtoList(List<Tenant> tenants);

    @Mapping(target = "status", source = "status", qualifiedByName = "stringToStatus")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Tenant toEntity(TenantDto dto);

    @Mapping(target = "status", source = "status", qualifiedByName = "stringToStatus")
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "siteCode", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateEntity(TenantDto dto, @MappingTarget Tenant tenant);

    @Named("statusToString")
    default String statusToString(SiteStatus status) {
        return status != null ? status.name() : null;
    }

    @Named("stringToStatus")
    default SiteStatus stringToStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return null;
        }
        try {
            return SiteStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
