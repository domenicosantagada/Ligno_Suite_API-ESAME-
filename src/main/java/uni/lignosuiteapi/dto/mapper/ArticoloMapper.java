package uni.lignosuiteapi.dto.mapper;

import org.mapstruct.Mapper;
import uni.lignosuiteapi.dto.ArticoloDTO;
import uni.lignosuiteapi.model.Articolo;

@Mapper(componentModel = "spring")
public interface ArticoloMapper {

    ArticoloDTO toDTO(Articolo articolo);

    Articolo toEntity(ArticoloDTO dto);
}
