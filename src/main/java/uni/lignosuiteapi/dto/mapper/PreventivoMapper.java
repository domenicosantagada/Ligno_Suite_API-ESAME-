package uni.lignosuiteapi.dto.mapper;

import org.mapstruct.Mapper;
import uni.lignosuiteapi.dto.PreventivoDTO;
import uni.lignosuiteapi.dto.PreventivoItemDTO;
import uni.lignosuiteapi.dto.PreventivoListDTO;
import uni.lignosuiteapi.model.Preventivo;
import uni.lignosuiteapi.model.PreventivoItem;

@Mapper(componentModel = "spring")
public interface PreventivoMapper {

    PreventivoDTO toDTO(Preventivo preventivo);

    Preventivo toEntity(PreventivoDTO dto);

    // Mapper specifico e leggero per la lista riassuntiva
    PreventivoListDTO toListDTO(Preventivo preventivo);

    PreventivoItemDTO itemToDTO(PreventivoItem item);

    PreventivoItem itemToEntity(PreventivoItemDTO dto);
}
