import { useCallback, useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { listar, excluir } from '../api/doadores'
import { mensagemDoErro } from '../api/cliente'
import Aviso from '../componentes/Aviso'
import BadgeStatus from '../componentes/BadgeStatus'
import Botao from '../componentes/Botao'
import Campo from '../componentes/Campo'
import Carregando from '../componentes/Carregando'
import EstadoVazio from '../componentes/EstadoVazio'
import ModalConfirmacao from '../componentes/ModalConfirmacao'
import { TIPOS_SANGUINEOS, tipoCompleto } from '../formatacao'
import { lerSessao } from '../sessao'

const ESPERA_DA_BUSCA_EM_MS = 300
const OPCOES_TIPO = [{ valor: '', rotulo: 'Todos' }, ...TIPOS_SANGUINEOS.map((tipo) => ({ valor: tipo, rotulo: tipo }))]

export default function Doadores() {
  const navegar = useNavigate()
  const ehAdmin = lerSessao().perfil === 'ADMIN'
  const [busca, setBusca] = useState('')
  const [buscaAplicada, setBuscaAplicada] = useState('')
  const [tipo, setTipo] = useState('')
  const [doadores, setDoadores] = useState(null)
  const [recarga, setRecarga] = useState(0)
  const [paraExcluir, setParaExcluir] = useState(null)
  const [aviso, setAviso] = useState(null)
  const fecharAviso = useCallback(() => setAviso(null), [])

  useEffect(() => {
    const temporizador = setTimeout(() => setBuscaAplicada(busca.trim()), ESPERA_DA_BUSCA_EM_MS)
    return () => clearTimeout(temporizador)
  }, [busca])

  useEffect(() => {
    let atual = true
    listar({ busca: buscaAplicada, tipoSanguineo: tipo })
      .then((lista) => atual && setDoadores(lista))
      .catch((falha) => atual && setAviso({ tipo: 'erro', mensagem: mensagemDoErro(falha) }))
    return () => {
      atual = false
    }
  }, [buscaAplicada, tipo, recarga])

  async function confirmarExclusao() {
    const alvo = paraExcluir
    setParaExcluir(null)
    try {
      await excluir(alvo.id)
      setAviso({ tipo: 'sucesso', mensagem: `${alvo.nome} foi excluído.` })
      setRecarga((valor) => valor + 1)
    } catch (falha) {
      setAviso({ tipo: 'erro', mensagem: mensagemDoErro(falha) })
    }
  }

  const semFiltro = !buscaAplicada && !tipo

  return (
    <>
      <div className="cabecalho-pagina">
        <h1>Doadores</h1>
        {doadores?.length > 0 && (
          <Link to="/doadores/novo" className="botao botao--primario">
            Novo doador
          </Link>
        )}
      </div>

      <div className="filtros">
        <Campo
          rotulo="Buscar por nome ou CPF"
          type="search"
          value={busca}
          onChange={(evento) => setBusca(evento.target.value)}
        />
        <Campo rotulo="Tipo sanguíneo" opcoes={OPCOES_TIPO} value={tipo} onChange={(evento) => setTipo(evento.target.value)} />
      </div>

      {!doadores && <Carregando />}

      {doadores?.length === 0 &&
        (semFiltro ? (
          <EstadoVazio titulo="Nenhum doador cadastrado" descricao="Comece cadastrando o primeiro doador.">
            <p>
              <Botao onClick={() => navegar('/doadores/novo')}>Cadastrar o primeiro</Botao>
            </p>
          </EstadoVazio>
        ) : (
          <EstadoVazio titulo="Nenhum doador encontrado" descricao="Tente outro nome, CPF ou tipo sanguíneo." />
        ))}

      {doadores?.length > 0 && (
        <div className="tabela-rolagem" role="region" aria-label="Lista de doadores" tabIndex={0}>
          <table className="tabela">
            <thead>
              <tr>
                <th scope="col">Nome</th>
                <th scope="col">CPF</th>
                <th scope="col">Tipo sanguíneo</th>
                <th scope="col">Situação</th>
                <th scope="col">Ações</th>
              </tr>
            </thead>
            <tbody>
              {doadores.map((doador) => (
                <tr key={doador.id}>
                  <td>
                    <Link to={`/doadores/${doador.id}`}>{doador.nome}</Link>
                  </td>
                  <td>{doador.cpfMascarado}</td>
                  <td>{tipoCompleto(doador.tipoSanguineo, doador.fatorRh)}</td>
                  <td>
                    <BadgeStatus status={doador.status} aptoAPartirDe={doador.aptoAPartirDe} />
                  </td>
                  <td>
                    <div className="tabela__acoes">
                      <Link to={`/doadores/${doador.id}`} className="botao botao--secundario">
                        Ver
                      </Link>
                      <Link to={`/doadores/${doador.id}/editar`} className="botao botao--secundario">
                        Editar
                      </Link>
                      {ehAdmin && (
                        <Botao variante="secundario" onClick={() => setParaExcluir(doador)}>
                          Excluir
                        </Botao>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      <ModalConfirmacao
        aberto={Boolean(paraExcluir)}
        titulo="Excluir doador"
        mensagem={`Tem certeza que deseja excluir ${paraExcluir?.nome ?? ''}? Esta ação não pode ser desfeita.`}
        textoConfirmar="Excluir"
        aoConfirmar={confirmarExclusao}
        aoCancelar={() => setParaExcluir(null)}
      />
      {aviso && <Aviso tipo={aviso.tipo} mensagem={aviso.mensagem} aoFechar={fecharAviso} />}
    </>
  )
}
