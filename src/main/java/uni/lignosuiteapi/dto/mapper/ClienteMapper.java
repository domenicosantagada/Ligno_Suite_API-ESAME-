package uni.lignosuiteapi.dto.mapper;

import org.mapstruct.Mapper;
import uni.lignosuiteapi.dto.ClienteDTO;
import uni.lignosuiteapi.model.Cliente;

/**
 * Mapper per conversione tra Cliente e ClienteDTO.
 * Utilizza MapStruct per generare automaticamente il codice di mapping copiando solo i campi con lo stesso nome.
 */
@Mapper(componentModel = "spring")
public interface ClienteMapper {

    ClienteDTO toDTO(Cliente cliente);

    Cliente toEntity(ClienteDTO dto);
}
