package uni.lignosuiteapi.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uni.lignosuiteapi.dto.PreventivoDTO;
import uni.lignosuiteapi.dto.PreventivoItemDTO;
import uni.lignosuiteapi.dto.PreventivoListDTO;
import uni.lignosuiteapi.model.Preventivo;
import uni.lignosuiteapi.model.PreventivoItem;

/**
 * Mapper per conversione tra Preventivo e PreventivoDTO, e tra PreventivoItem e PreventivoItemDTO.
 * Utilizza MapStruct per generare automaticamente il codice di mapping copiando solo i campi con lo stesso nome.
 * Per i campi complessi (come l'utente o il preventivo padre) diciamo a MapStruct di ignorarli, perché li gestiamo noi manualmente nel service.
 */
@Mapper(componentModel = "spring")
public interface PreventivoMapper {

    @Mapping(source = "utente.logoBase64", target = "fromLogo")
    PreventivoDTO toDTO(Preventivo preventivo);

    // Diciamo a MapStruct di ignorare l'utente (lo settiamo noi nel service)
    @Mapping(target = "utente", ignore = true)
    Preventivo toEntity(PreventivoDTO dto);

    // Mapper specifico e leggero per la lista riassuntiva
    @Mapping(source = "utente.nomeAzienda", target = "fromName")
    PreventivoListDTO toListDTO(Preventivo preventivo);

    PreventivoItemDTO itemToDTO(PreventivoItem item);

    // Diciamo a MapStruct di ignorare il preventivo padre (lo settiamo noi nel service)
    @Mapping(target = "preventivo", ignore = true)
    PreventivoItem itemToEntity(PreventivoItemDTO dto);
}
