package com.clinica.Util;

public class ValidadorUtil {

    public static boolean isCpfValido(String cpf) {
        cpf = cpf.replaceAll("[^\\d]", "");

        if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) return false;

        try {
            int soma = 0;
            for (int i = 0; i < 9; i++) {
                soma += (cpf.charAt(i) - '0') * (10 - i);
            }

            int digito1 = 11 - (soma % 11);
            if (digito1 >= 10) digito1 = 0;

            if (digito1 != (cpf.charAt(9) - '0')) return false;

            soma = 0;
            for (int i = 0; i < 10; i++) {
                soma += (cpf.charAt(i) - '0') * (11 - i);
            }

            int digito2 = 11 - (soma % 11);
            if (digito2 >= 10) digito2 = 0;

            return digito2 == (cpf.charAt(10) - '0');
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isEmailValido(String email) {
        return email.matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$");
    }

    public static boolean isTelefoneValido(String telefone) {
        return telefone.matches("^[\\d\\s\\-()]+$");
    }

    public static boolean isEnderecoValido(String endereco) {
        return endereco.matches(".*\\d+.*");
    }
    
    public static boolean isCrmvValido(String crmv) {
        // O regex verifica:
        // ^CRMV-       : Inicia com "CRMV-"
        // [A-Z]{2}     : Duas letras maiúsculas (UF)
        // \\s          : Um espaço
        // \\d{5}       : Exatamente 5 dígitos
        // -            : Um hífen
        // [A-Z]$       : Uma letra maiúscula no final
        return crmv.matches("^CRMV-[A-Z]{2}\\s\\d{5}-[A-Z]$");
    }
}
