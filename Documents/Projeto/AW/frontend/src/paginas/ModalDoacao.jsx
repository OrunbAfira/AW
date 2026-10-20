import { useState } from 'react'
import { errosDeCampo, mensagemDoErro } from '../api/cliente'
import { registrar } from '../api/doacoes'
import Botao from '../componentes/Botao'
import Campo from '../componentes/Campo'
import Modal from '../componentes/Modal'
import { formatarData, hojeIso } from '../formatacao'

const PADRAO_PRESSAO = /^\d{2,3}\/\d{2,3}$/
const paraNumero = (texto) => Number(String(texto).replace(',', '.'))

function validar(valores) {
  const erros = {}
  if (!(paraNumero(valores.pesoEmQuilos) > 0)) erros.pesoEmQuilos = 'Informe o peso, maior que zero'
  if (!PADRAO_PRESSAO.test(valores.pressaoArterial.trim())) erros.pressaoArterial = 'Use o formato 120/80'
  if (!(paraNumero(valores.hemoglobina) > 0)) erros.hemoglobina = 'Informe a hemoglobina, maior que zero'
  if (!(Number(valores.volumeEmMililitros) > 0)) erros.volumeEmMililitros = 'Informe o volume, maior que zero'
  return erros
}

export default function ModalDoacao({ aberto, idDoador, pesoInicial, aoFechar, aoSucesso }) {
  const [valores, setValores] = useState({
    pesoEmQuilos: pesoInicial ? String(pesoInicial) : '',
    pressaoArterial: '',
    hemoglobina: '',
    volumeEmMililitros: '450',
  })
  const [erros, setErros] = useState({})
  const [bloqueio, setBloqueio] = useState(null)
  const [mensagemGeral, setMensagemGeral] = useState('')
  const [enviando, setEnviando] = useState(false)

  const alterar = (campo) => (evento) => setValores((atual) => ({ ...atual, [campo]: evento.target.value }))

  async function enviar(evento) {
    evento.preventDefault()
    setBloqueio(null)
    setMensagemGeral('')
    const encontrados = validar(valores)
    setErros(encontrados)
    if (Object.keys(encontrados).length > 0) return

    setEnviando(true)
    try {
      const doacao = await registrar(idDoador, {
        pesoEmQuilos: paraNumero(valores.pesoEmQuilos),
        pressaoArterial: valores.pressaoArterial.trim(),
        hemoglobina: paraNumero(valores.hemoglobina),
        volumeEmMililitros: Number(valores.volumeEmMililitros),
      })
      aoSucesso(doacao)
    } catch (falha) {
      if (falha.response?.status === 422) {
        setBloqueio(falha.response.data)
        return
      }
      const doServidor = errosDeCampo(falha)
      setErros(doServidor)
      if (Object.keys(doServidor).length === 0) setMensagemGeral(mensagemDoErro(falha))
    } finally {
      setEnviando(false)
    }
  }

  return (
    <Modal aberto={aberto} titulo="Registrar doação" aoFechar={aoFechar}>
      <form onSubmit={enviar} noValidate>
        {bloqueio && (
          <div className="bloqueio" role="alert">
            <strong>Doação não permitida</strong>
            <ul>
              {bloqueio.motivos.map((motivo) => (
                <li key={motivo}>{motivo}</li>
              ))}
            </ul>
            {bloqueio.proximaDataApta && (
              <p>
                Apto a partir de <strong>{formatarData(bloqueio.proximaDataApta)}</strong>.
              </p>
            )}
          </div>
        )}
        {mensagemGeral && (
          <p className="bloqueio" role="alert">
            {mensagemGeral}
          </p>
        )}
        <Campo
          rotulo="Data da doação"
          value={formatarData(hojeIso())}
          readOnly
          dica="A doação é registrada com a data de hoje."
        />
        <Campo
          rotulo="Peso (kg)"
          inputMode="decimal"
          value={valores.pesoEmQuilos}
          onChange={alterar('pesoEmQuilos')}
          erro={erros.pesoEmQuilos}
        />
        <Campo
          rotulo="Pressão arterial (mmHg)"
          placeholder="120/80"
          value={valores.pressaoArterial}
          onChange={alterar('pressaoArterial')}
          erro={erros.pressaoArterial}
        />
        <Campo
          rotulo="Hemoglobina (g/dL)"
          inputMode="decimal"
          value={valores.hemoglobina}
          onChange={alterar('hemoglobina')}
          erro={erros.hemoglobina}
        />
        <Campo
          rotulo="Volume (mL)"
          inputMode="numeric"
          value={valores.volumeEmMililitros}
          onChange={alterar('volumeEmMililitros')}
          erro={erros.volumeEmMililitros}
        />
        <div className="modal__acoes">
          <Botao variante="secundario" onClick={aoFechar}>
            Cancelar
          </Botao>
          <Botao type="submit" carregando={enviando}>
            Registrar
          </Botao>
        </div>
      </form>
    </Modal>
  )
}
