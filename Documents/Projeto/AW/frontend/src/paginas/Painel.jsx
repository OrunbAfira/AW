import { useCallback, useEffect, useState } from 'react'
import { mensagemDoErro } from '../api/cliente'
import { obter } from '../api/painel'
import Aviso from '../componentes/Aviso'
import Carregando from '../componentes/Carregando'

export default function Painel() {
  const [dados, setDados] = useState(null)
  const [erro, setErro] = useState('')
  const fecharErro = useCallback(() => setErro(''), [])

  useEffect(() => {
    obter().then(setDados).catch((falha) => setErro(mensagemDoErro(falha)))
  }, [])

  return (
    <>
      <h1>Painel</h1>
      {!dados && !erro && <Carregando />}
      {dados && (
        <dl className="indicadores">
          <div className="indicador">
            <dt>Total de doadores</dt>
            <dd>{dados.totalDoadores}</dd>
          </div>
          <div className="indicador">
            <dt>Aptos hoje</dt>
            <dd>{dados.aptosHoje}</dd>
          </div>
          <div className="indicador">
            <dt>Doações no mês</dt>
            <dd>{dados.doacoesNoMes}</dd>
          </div>
        </dl>
      )}
      {erro && <Aviso tipo="erro" mensagem={erro} aoFechar={fecharErro} />}
    </>
  )
}
