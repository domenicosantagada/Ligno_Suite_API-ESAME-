package uni.lignosuiteapi.dto.mapper;

import org.mapstruct.Mapper;
import uni.lignosuiteapi.dto.ClienteDTO;
import uni.lignosuiteapi.model.Cliente;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    ClienteDTO toDTO(Cliente cliente);

    Cliente toEntity(ClienteDTO dto);
}
