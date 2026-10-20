package br.umc.hemovida.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record Cpf(@Column(name = "cpf", nullable = false, unique = true, length = 11) String numero) {

    public Cpf {
        if (numero == null) {
            throw new IllegalArgumentException("CPF é obrigatório");
        }
        numero = numero.replaceAll("\\D", "");
        if (numero.length() != 11 || numero.chars().distinct().count() == 1
                || numero.charAt(9) - '0' != digitoVerificador(numero, 9)
                || numero.charAt(10) - '0' != digitoVerificador(numero, 10)) {
            throw new IllegalArgumentException("CPF inválido");
        }
    }

    // Evita que o CPF apareça em logs e mensagens de erro.
    @Override
    public String toString() {
        return "Cpf[***]";
    }

    private static int digitoVerificador(String numero, int quantidade) {
        int soma = 0;
        for (int i = 0; i < quantidade; i++) {
            soma += (numero.charAt(i) - '0') * (quantidade + 1 - i);
        }
        int resto = soma * 10 % 11;
        return resto == 10 ? 0 : resto;
    }
}
