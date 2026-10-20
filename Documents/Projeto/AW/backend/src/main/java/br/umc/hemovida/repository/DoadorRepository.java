package br.umc.hemovida.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.umc.hemovida.model.Doador;
import br.umc.hemovida.model.TipoSanguineo;

public interface DoadorRepository extends JpaRepository<Doador, Long> {

    // "digitos" é o termo só com números (CPF digitado com ou sem máscara); tipoSanguineo nulo desliga o filtro.
    @Query("""
            select d from Doador d
            where (lower(d.nome) like lower(concat('%', :termo, '%'))
                   or (:digitos <> '' and d.cpf.numero like concat('%', :digitos, '%')))
              and (:tipoSanguineo is null or d.tipoSanguineo = :tipoSanguineo)
            order by d.nome
            """)
    List<Doador> buscarPorNomeOuCpf(@Param("termo") String termo, @Param("digitos") String digitos,
            @Param("tipoSanguineo") TipoSanguineo tipoSanguineo);

    Optional<Doador> findByCpfNumero(String numero);

    // Mesma regra de statusEm(hoje) == APTO.
    @Query("""
            select count(d) from Doador d left join d.fichaClinica f
            where f is null
               or (f.impedimentoDefinitivo = false and (f.dataProximaDoacao is null or f.dataProximaDoacao <= :hoje))
            """)
    long contarAptosEm(@Param("hoje") LocalDate hoje);
}
