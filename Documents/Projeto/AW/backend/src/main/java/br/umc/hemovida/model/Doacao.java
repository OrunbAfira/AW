package br.umc.hemovida.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Doacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private Doador doador;

    @Column(nullable = false)
    private LocalDate dataDoacao;

    @Column(nullable = false, unique = true)
    private String numeroBolsa;

    @Column(nullable = false)
    private Integer volumeEmMililitros;

    private String observacoes;

    private String pressaoArterial;

    private Double hemoglobina;

    protected Doacao() {
    }

    private Doacao(Construtor construtor) {
        this.doador = construtor.doador;
        this.dataDoacao = construtor.dataDoacao;
        this.numeroBolsa = construtor.numeroBolsa;
        this.volumeEmMililitros = construtor.volumeEmMililitros;
        this.observacoes = construtor.observacoes;
        this.pressaoArterial = construtor.pressaoArterial;
        this.hemoglobina = construtor.hemoglobina;
    }

    public static Construtor builder() {
        return new Construtor();
    }

    public Long getId() { return id; }

    public Doador getDoador() { return doador; }

    public LocalDate getDataDoacao() { return dataDoacao; }

    public String getNumeroBolsa() { return numeroBolsa; }

    public Integer getVolumeEmMililitros() { return volumeEmMililitros; }

    public String getObservacoes() { return observacoes; }

    public String getPressaoArterial() { return pressaoArterial; }

    public Double getHemoglobina() { return hemoglobina; }

    public static class Construtor {
        private Doador doador;
        private LocalDate dataDoacao;
        private String numeroBolsa;
        private Integer volumeEmMililitros;
        private String observacoes;
        private String pressaoArterial;
        private Double hemoglobina;

        public Construtor doador(Doador doador) { this.doador = doador; return this; }

        public Construtor dataDoacao(LocalDate dataDoacao) { this.dataDoacao = dataDoacao; return this; }

        public Construtor numeroBolsa(String numeroBolsa) { this.numeroBolsa = numeroBolsa; return this; }

        public Construtor volumeEmMililitros(Integer volumeEmMililitros) {
            this.volumeEmMililitros = volumeEmMililitros;
            return this;
        }

        public Construtor observacoes(String observacoes) { this.observacoes = observacoes; return this; }

        public Construtor pressaoArterial(String pressaoArterial) { this.pressaoArterial = pressaoArterial; return this; }

        public Construtor hemoglobina(Double hemoglobina) { this.hemoglobina = hemoglobina; return this; }

        public Doacao build() {
            if (doador == null) throw new IllegalArgumentException("Doador é obrigatório");
            if (dataDoacao == null) throw new IllegalArgumentException("Data da doação é obrigatória");
            if (numeroBolsa == null || numeroBolsa.isBlank()) {
                throw new IllegalArgumentException("Número da bolsa é obrigatório");
            }
            if (volumeEmMililitros == null || volumeEmMililitros <= 0) {
                throw new IllegalArgumentException("Volume deve ser maior que zero");
            }
            return new Doacao(this);
        }
    }
}
