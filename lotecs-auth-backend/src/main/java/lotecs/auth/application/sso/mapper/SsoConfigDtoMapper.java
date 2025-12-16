package lotecs.auth.application.sso.mapper;

import lotecs.auth.application.sso.dto.SsoConfigDto;
import lotecs.auth.domain.sso.SsoType;
import lotecs.auth.domain.sso.model.TenantSsoConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface SsoConfigDtoMapper {

    @Mapping(target = "ssoType", expression = "java(config.getSsoType() != null ? config.getSsoType().name() : null)")
    SsoConfigDto toDto(TenantSsoConfig config);

    @Mapping(target = "ssoType", expression = "java(parseSsoType(dto.getSsoType()))")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    TenantSsoConfig toEntity(SsoConfigDto dto);

    default SsoType parseSsoType(String ssoType) {
        if (ssoType == null || ssoType.trim().isEmpty()) {
            return SsoType.INTERNAL;
        }
        return SsoType.valueOf(ssoType.toUpperCase());
    }
}
