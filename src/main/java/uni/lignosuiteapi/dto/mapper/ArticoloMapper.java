package uni.lignosuiteapi.dto.mapper;

import org.mapstruct.Mapper;
import uni.lignosuiteapi.dto.ArticoloDTO;
import uni.lignosuiteapi.model.Articolo;

/**
 * Mapper per conversione tra Articolo e ArticoloDTO.
 * Utilizza MapStruct per generare automaticamente il codice di mapping copiando solo i campi con lo stesso nome.
 */
@Mapper(componentModel = "spring")
public interface ArticoloMapper {

    ArticoloDTO toDTO(Articolo articolo);

    Articolo toEntity(ArticoloDTO dto);
}
