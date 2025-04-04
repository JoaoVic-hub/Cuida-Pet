// EnderecoAdapter.java
package com.clinica.adapter;

import com.clinica.DTO.EnderecoViaCepDTO;

public class EnderecoAdapter {
    public static String fromViaCepDTO(EnderecoViaCepDTO dto) {
        if (dto == null) return null;
        // Formata o endereço no padrão "logradouro, bairro, localidade – uf"
        return String.format("%s, %s, %s – %s", 
                             dto.getLogradouro(), 
                             dto.getBairro(), 
                             dto.getLocalidade(), 
                             dto.getUf());
    }
}
