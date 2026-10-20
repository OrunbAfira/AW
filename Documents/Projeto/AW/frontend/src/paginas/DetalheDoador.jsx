import { useCallback, useEffect, useState } from 'react'
import { Link, useLocation, useParams } from 'react-router-dom'
import { mensagemDoErro } from '../api/cliente'
import { aptidao as consultarAptidao, buscar } from '../api/doadores'
import { listarDoDoador } from '../api/doacoes'
import Aviso from '../componentes/Aviso'
import BadgeStatus from '../componentes/BadgeStatus'
import Botao from '../componentes/Botao'
import Carregando from '../componentes/Carregando'
import EstadoVazio from '../componentes/EstadoVazio'
import { formatarData, formatarNumero, mascararCpf, mascararTelefone, tipoCompleto } from '../formatacao'
import ModalDoacao from './ModalDoacao'

const SEXO = { MASCULINO: 'Masculino', FEMININO: 'Feminino' }

function Item({ rotulo, children }) {
  return (
    <div className="dados__item">
      <dt>{rotulo}</dt>
      <dd>{children}</dd>
    </div>
  )
}

export default function DetalheDoador() {
  const { id } = useParams()
  const localizacao = useLocation()
  const [doador, setDoador] = useState(null)
  const [aptidao, setAptidao] = useState(null)
  const [doacoes, setDoacoes] = useState([])
  const [modalAberto, setModalAberto] = useState(false)
  const [chaveDoModal, setChaveDoModal] = useState(0)
  const [aviso, setAviso] = useState(
    localizacao.state?.aviso ? { tipo: 'sucesso', mensagem: localizacao.state.aviso } : null,
  )
  const fecharAviso = useCallback(() => setAviso(null), [])

  const carregar = useCallback(async () => {
    try {
      const [dadosDoador, dadosAptidao, dadosDoacoes] = await Promise.all([
        buscar(id),
        consultarAptidao(id),
        listarDoDoador(id),
      ])
      setDoador(dadosDoador)
      setAptidao(dadosAptidao)
      setDoacoes(dadosDoacoes)
    } catch (falha) {
      setAviso({ tipo: 'erro', mensagem: mensagemDoErro(falha) })
    }
  }, [id])

  useEffect(() => {
    carregar()
  }, [carregar])

  function aoRegistrar(doacao) {
    setModalAberto(false)
    setChaveDoModal((chave) => chave + 1) // zera o formulário do modal
    setAviso({ tipo: 'sucesso', mensagem: `Doação registrada. Bolsa ${doacao.numeroBolsa}.` })
    carregar()
  }

  if (!doador || !aptidao) {
    return (
      <>
        {aviso?.tipo !== 'erro' && <Carregando />}
        {aviso && <Aviso tipo={aviso.tipo} mensagem={aviso.mensagem} aoFechar={fecharAviso} />}
      </>
    )
  }

  // A situação vem da verificação completa (todas as regras), a mesma do cartão de aptidão.
  const statusGeral = aptidao.apto ? 'APTO' : aptidao.proximaDataApta ? 'INAPTO_TEMPORARIO' : 'INAPTO_DEFINITIVO'
  const corDoCartao = aptidao.apto ? 'verde' : aptidao.proximaDataApta ? 'amarelo' : 'vermelho'
  const ficha = doador.fichaClinica

  return (
    <>
      <div className="cabecalho-pagina">
        <h1>
          {doador.nome} <BadgeStatus status={statusGeral} aptoAPartirDe={aptidao.proximaDataApta} />
        </h1>
        <div className="cabecalho-pagina__acoes">
          <Link to={`/doadores/${doador.id}/editar`} className="botao botao--secundario">
            Editar
          </Link>
          <Botao
            disabled={!aptidao.apto}
            aria-describedby={aptidao.apto ? undefined : 'motivo-bloqueio'}
            onClick={() => setModalAberto(true)}
          >
            Registrar doação
          </Botao>
        </div>
      </div>
      {!aptidao.apto && (
        <p id="motivo-bloqueio" className="dica">
          Registro indisponível: {aptidao.motivos.join('; ')}
          {aptidao.proximaDataApta && ` Apto a partir de ${formatarData(aptidao.proximaDataApta)}.`}
        </p>
      )}

      <section className={`cartao cartao--${corDoCartao}`} aria-labelledby="titulo-aptidao">
        <h2 id="titulo-aptidao">Aptidão para doar</h2>
        {aptidao.apto ? (
          <p>Apto para doar hoje.</p>
        ) : (
          <>
            <p>Inapto no momento:</p>
            <ul>
              {aptidao.motivos.map((motivo) => (
                <li key={motivo}>{motivo}</li>
              ))}
            </ul>
            {aptidao.proximaDataApta && (
              <p>
                Apto a partir de <strong>{formatarData(aptidao.proximaDataApta)}</strong>.
              </p>
            )}
          </>
        )}
      </section>

      <div className="duas-colunas">
        <section className="cartao" aria-labelledby="titulo-dados">
          <h2 id="titulo-dados">Dados pessoais</h2>
          <dl className="dados">
            <Item rotulo="CPF">{mascararCpf(doador.cpf)}</Item>
            <Item rotulo="Data de nascimento">{formatarData(doador.dataNascimento)}</Item>
            <Item rotulo="Sexo">{SEXO[doador.sexo]}</Item>
            <Item rotulo="Tipo sanguíneo">{tipoCompleto(doador.tipoSanguineo, doador.fatorRh)}</Item>
            <Item rotulo="Telefone">{doador.telefone ? mascararTelefone(doador.telefone) : '—'}</Item>
          </dl>
        </section>
        <section className="cartao" aria-labelledby="titulo-ficha">
          <h2 id="titulo-ficha">Ficha clínica</h2>
          <dl className="dados">
            <Item rotulo="Peso">{ficha?.pesoEmQuilos ? `${formatarNumero(ficha.pesoEmQuilos)} kg` : '—'}</Item>
            <Item rotulo="Impedimento definitivo">{ficha?.impedimentoDefinitivo ? 'Sim' : 'Não'}</Item>
            <Item rotulo="Última tatuagem">{formatarData(ficha?.dataUltimaTatuagem)}</Item>
            <Item rotulo="Próxima doação a partir de">{formatarData(ficha?.dataProximaDoacao)}</Item>
          </dl>
        </section>
      </div>

      <section className="cartao" aria-labelledby="titulo-doacoes">
        <h2 id="titulo-doacoes">Doações</h2>
        {doacoes.length === 0 ? (
          <EstadoVazio titulo="Nenhuma doação registrada" descricao="As doações deste doador aparecerão aqui." />
        ) : (
          <ol className="linha-do-tempo">
            {doacoes.map((doacao) => (
              <li key={doacao.id}>
                <strong>{formatarData(doacao.dataDoacao)}</strong> · Bolsa {doacao.numeroBolsa}
                <div className="linha-do-tempo__detalhe">
                  {[
                    `${formatarNumero(doacao.volumeEmMililitros)} mL`,
                    doacao.pressaoArterial && `Pressão ${doacao.pressaoArterial}`,
                    doacao.hemoglobina && `Hemoglobina ${formatarNumero(doacao.hemoglobina)} g/dL`,
                  ]
                    .filter(Boolean)
                    .join(' · ')}
                </div>
                {doacao.observacoes && <div className="linha-do-tempo__detalhe">{doacao.observacoes}</div>}
              </li>
            ))}
          </ol>
        )}
      </section>

      <ModalDoacao
        key={chaveDoModal}
        aberto={modalAberto}
        idDoador={doador.id}
        pesoInicial={ficha?.pesoEmQuilos}
        aoFechar={() => setModalAberto(false)}
        aoSucesso={aoRegistrar}
      />
      {aviso && <Aviso tipo={aviso.tipo} mensagem={aviso.mensagem} aoFechar={fecharAviso} />}
    </>
  )
}
