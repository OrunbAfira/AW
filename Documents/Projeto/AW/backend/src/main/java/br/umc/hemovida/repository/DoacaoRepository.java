package br.umc.hemovida.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.umc.hemovida.model.Doacao;

public interface DoacaoRepository extends JpaRepository<Doacao, Long> {

    List<Doacao> findByDoadorIdOrderByDataDoacaoDesc(Long doadorId);

    Optional<Doacao> findTopByNumeroBolsaStartingWithOrderByNumeroBolsaDesc(String prefixo);

    long countByDataDoacaoBetween(LocalDate inicio, LocalDate fim);
}
