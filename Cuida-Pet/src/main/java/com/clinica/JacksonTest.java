package com.clinica;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;

public class JacksonTest {
    public static void main(String[] args) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            
            LocalDate hoje = LocalDate.now();
            String json = mapper.writeValueAsString(hoje);
            System.out.println("✅ Data serializada: " + json);
            
            LocalDate dataDesserializada = mapper.readValue(json, LocalDate.class);
            System.out.println("✅ Data desserializada: " + dataDesserializada);
            
        } catch (Exception e) {
            System.err.println("❌ Erro no teste Jackson:");
            e.printStackTrace();
            
            System.out.println("\n🔍 Verificando classpath:");
            String classpath = System.getProperty("java.class.path");
            System.out.println("Classpath: " + classpath);
        }
    }
}