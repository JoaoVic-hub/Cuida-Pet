package com.clinica.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JsonPersistenceHelper<T> {

    private final ObjectMapper objectMapper;
    private final String filePath;
    private final TypeReference<List<T>> typeReference;

    // Interface interna para auxiliar na geração de IDs
    public interface Identifiable {
        int getId();
        void setId(int id);
    }

    public JsonPersistenceHelper(String fileName, TypeReference<List<T>> typeReference) {
        // Garante que o diretório de dados exista
        String dataDirectory = "./data/"; // Diretório na raiz do projeto
        try {
            Files.createDirectories(Paths.get(dataDirectory));
        } catch (IOException e) {
            System.err.println("Erro crítico ao criar diretório de dados: " + dataDirectory + " - " + e.getMessage());
            // Lançar uma exceção aqui pode ser apropriado dependendo da aplicação
        }

        this.filePath = dataDirectory + fileName;
        this.objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // Formata o JSON
        objectMapper.registerModule(new JavaTimeModule()); // Suporte a Java Time API (LocalDate, LocalDateTime)
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Datas como string ISO
        this.typeReference = typeReference;
    }

    public List<T> readAll() {
        File file = new File(filePath);
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }
        try {
            // Lê a lista de objetos do arquivo JSON
            return objectMapper.readValue(file, typeReference);
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo JSON: " + filePath + " - " + e.getMessage());
            e.printStackTrace(); // Ajuda a depurar
            return new ArrayList<>(); // Retorna lista vazia em caso de erro de leitura
        }
    }

    public void writeAll(List<T> data) {
        try {
            // Escreve a lista de objetos no arquivo JSON
            objectMapper.writeValue(new File(filePath), data);
        } catch (IOException e) {
            System.err.println("Erro ao escrever no arquivo JSON: " + filePath + " - " + e.getMessage());
            e.printStackTrace(); // Ajuda a depurar
        }
    }

    // Gera o próximo ID sequencial baseado no maior ID existente na lista
    public int getNextId(List<? extends Identifiable> items) {
        if (items == null || items.isEmpty()) {
            return 1; // Primeiro ID é 1
        }
        return items.stream()
                    .mapToInt(Identifiable::getId)
                    .max()
                    .orElse(0) + 1; // Pega o máximo e incrementa, ou começa de 1 se não houver máximo
    }
}