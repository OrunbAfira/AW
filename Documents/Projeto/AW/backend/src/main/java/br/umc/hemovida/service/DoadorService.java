package br.umc.hemovida.service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.umc.hemovida.dto.DoadorRequest;
import br.umc.hemovida.dto.DoadorResponse;
import br.umc.hemovida.dto.DoadorResumo;
import br.umc.hemovida.excecao.CampoInvalidoException;
import br.umc.hemovida.excecao.ConflitoException;
import br.umc.hemovida.excecao.RecursoNaoEncontradoException;
import br.umc.hemovida.model.Cpf;
import br.umc.hemovida.model.Doador;
import br.umc.hemovida.model.FichaClinica;
import br.umc.hemovida.model.TipoSanguineo;
import br.umc.hemovida.repository.DoadorRepository;

@Service
public class DoadorService {

    private final DoadorRepository doadorRepository;
    private final Clock relogio;

    public DoadorService(DoadorRepository doadorRepository, Clock relogio) {
        this.doadorRepository = doadorRepository;
        this.relogio = relogio;
    }

    @Transactional(readOnly = true)
    public List<DoadorResumo> listar(String busca, TipoSanguineo tipoSanguineo) {
        String termo = busca == null ? "" : busca.trim();
        LocalDate hoje = LocalDate.now(relogio);
        return doadorRepository.buscarPorNomeOuCpf(termo, termo.replaceAll("\\D", ""), tipoSanguineo).stream()
                .map(doador -> DoadorResumo.de(doador, hoje))
                .toList();
    }

    @Transactional(readOnly = true)
    public DoadorResponse buscar(Long id) {
        return DoadorResponse.de(carregar(id), LocalDate.now(relogio));
    }

    @Transactional
    public DoadorResponse criar(DoadorRequest pedido) {
        Cpf cpf = cpfLivre(pedido.cpf(), null);
        DoadorRequest.FichaClinicaRequest ficha = pedido.fichaClinica();
        Doador doador = new Doador(pedido.nome().trim(), cpf, pedido.dataNascimento(), pedido.sexo(),
                pedido.tipoSanguineo(), pedido.fatorRh(), pedido.telefone(),
                new FichaClinica(ficha.pesoEmQuilos(), ficha.impedimentoDefinitivo(), null,
                        ficha.dataUltimaTatuagem()));
        return DoadorResponse.de(doadorRepository.save(doador), LocalDate.now(relogio));
    }

    @Transactional
    public DoadorResponse atualizar(Long id, DoadorRequest pedido) {
        Doador doador = carregar(id);
        Cpf cpf = cpfLivre(pedido.cpf(), id);
        DoadorRequest.FichaClinicaRequest ficha = pedido.fichaClinica();
        doador.atualizar(pedido.nome().trim(), cpf, pedido.dataNascimento(), pedido.sexo(), pedido.tipoSanguineo(),
                pedido.fatorRh(), pedido.telefone(), ficha.pesoEmQuilos(), ficha.impedimentoDefinitivo(),
                ficha.dataUltimaTatuagem());
        return DoadorResponse.de(doador, LocalDate.now(relogio));
    }

    @Transactional
    public void excluir(Long id) {
        Doador doador = carregar(id);
        // Doações são registro de bolsas de sangue: não são apagadas junto com o doador.
        if (!doador.historico().estaVazio()) {
            throw new ConflitoException("Doador com doações registradas não pode ser excluído");
        }
        doadorRepository.delete(doador);
    }

    private Doador carregar(Long id) {
        return doadorRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Doador não encontrado"));
    }

    private Cpf cpfLivre(String texto, Long idAtual) {
        Cpf cpf;
        try {
            cpf = new Cpf(texto);
        } catch (IllegalArgumentException excecao) {
            throw new CampoInvalidoException("cpf", "CPF inválido");
        }
        boolean emUsoPorOutro = doadorRepository.findByCpfNumero(cpf.numero())
                .filter(outro -> !outro.getId().equals(idAtual))
                .isPresent();
        if (emUsoPorOutro) {
            throw new CampoInvalidoException("cpf", "CPF já cadastrado");
        }
        return cpf;
    }
}
