package br.umc.hemovida.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class Doador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Embedded
    private Cpf cpf;

    @Column(nullable = false)
    private LocalDate dataNascimento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Sexo sexo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoSanguineo tipoSanguineo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FatorRh fatorRh;

    private String telefone;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private FichaClinica fichaClinica;

    @OneToMany(mappedBy = "doador")
    private List<Doacao> doacoes = new ArrayList<>();

    protected Doador() {
    }

    public Doador(String nome, Cpf cpf, LocalDate dataNascimento, Sexo sexo, TipoSanguineo tipoSanguineo,
            FatorRh fatorRh, String telefone, FichaClinica fichaClinica) {
        this.nome = nome;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.sexo = sexo;
        this.tipoSanguineo = tipoSanguineo;
        this.fatorRh = fatorRh;
        this.telefone = telefone;
        this.fichaClinica = fichaClinica;
    }

    public StatusDoador statusEm(LocalDate data) {
        if (fichaClinica == null) {
            return StatusDoador.APTO;
        }
        if (fichaClinica.isImpedimentoDefinitivo()) {
            return StatusDoador.INAPTO_DEFINITIVO;
        }
        LocalDate proxima = fichaClinica.getDataProximaDoacao();
        return proxima != null && proxima.isAfter(data) ? StatusDoador.INAPTO_TEMPORARIO : StatusDoador.APTO;
    }

    public void atualizar(String nome, Cpf cpf, LocalDate dataNascimento, Sexo sexo, TipoSanguineo tipoSanguineo,
            FatorRh fatorRh, String telefone, Double pesoEmQuilos, boolean impedimentoDefinitivo,
            LocalDate dataUltimaTatuagem) {
        this.nome = nome;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.sexo = sexo;
        this.tipoSanguineo = tipoSanguineo;
        this.fatorRh = fatorRh;
        this.telefone = telefone;
        if (fichaClinica == null) {
            fichaClinica = new FichaClinica(pesoEmQuilos, impedimentoDefinitivo, null, dataUltimaTatuagem);
            return;
        }
        fichaClinica.atualizarDadosClinicos(pesoEmQuilos, impedimentoDefinitivo, dataUltimaTatuagem);
    }

    public HistoricoDoacoes historico() {
        return new HistoricoDoacoes(doacoes);
    }

    public void atualizarAposDoacao(Double pesoEmQuilos, LocalDate dataProximaDoacao) {
        if (fichaClinica == null) {
            fichaClinica = new FichaClinica(pesoEmQuilos, false, dataProximaDoacao);
            return;
        }
        fichaClinica.atualizarAposDoacao(pesoEmQuilos, dataProximaDoacao);
    }

    public Long getId() { return id; }

    public String getNome() { return nome; }

    public Cpf getCpf() { return cpf; }

    public LocalDate getDataNascimento() { return dataNascimento; }

    public Sexo getSexo() { return sexo; }

    public TipoSanguineo getTipoSanguineo() { return tipoSanguineo; }

    public FatorRh getFatorRh() { return fatorRh; }

    public String getTelefone() { return telefone; }

    public FichaClinica getFichaClinica() { return fichaClinica; }

    public List<Doacao> getDoacoes() { return doacoes; }
}
