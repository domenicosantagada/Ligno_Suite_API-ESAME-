package uni.lignosuiteapi.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uni.lignosuiteapi.dto.PreventivoDTO;
import uni.lignosuiteapi.dto.PreventivoItemDTO;
import uni.lignosuiteapi.dto.PreventivoListDTO;
import uni.lignosuiteapi.model.Preventivo;
import uni.lignosuiteapi.model.PreventivoItem;

@Mapper(componentModel = "spring")
public interface PreventivoMapper {

    PreventivoDTO toDTO(Preventivo preventivo);

    // Diciamo a MapStruct di ignorare l'utente (lo settiamo noi nel service)
    @Mapping(target = "utente", ignore = true)
    Preventivo toEntity(PreventivoDTO dto);

    // Mapper specifico e leggero per la lista riassuntiva
    PreventivoListDTO toListDTO(Preventivo preventivo);

    PreventivoItemDTO itemToDTO(PreventivoItem item);

    // Diciamo a MapStruct di ignorare il preventivo padre (lo settiamo noi nel service)
    @Mapping(target = "preventivo", ignore = true)
    PreventivoItem itemToEntity(PreventivoItemDTO dto);
}
