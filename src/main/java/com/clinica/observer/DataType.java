package com.clinica.observer; // Ou o pacote que preferir (ex: com.clinica.util)

/**
 * Enum para identificar o tipo de dado que foi modificado,
 * usado na notificação do padrão Observer.
 */
public enum DataType {
    CLIENTE,
    ANIMAL,
    VETERINARIO,
    CONSULTA,
    AGENDA, // Pode ser usado se a agenda puder ser modificada diretamente
    PRONTUARIO, // Adicionado para completude
    EMPRESA; // Adicionado para completude
}
