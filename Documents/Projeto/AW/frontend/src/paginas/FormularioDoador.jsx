import { useCallback, useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { errosDeCampo, mensagemDoErro } from '../api/cliente'
import { atualizar, buscar, criar } from '../api/doadores'
import Aviso from '../componentes/Aviso'
import Botao from '../componentes/Botao'
import Campo from '../componentes/Campo'
import Carregando from '../componentes/Carregando'
import {
  TIPOS_SANGUINEOS,
  apenasDigitos,
  cpfValido,
  hojeIso,
  mascararCpf,
  mascararTelefone,
} from '../formatacao'

const VAZIO = {
  nome: '',
  cpf: '',
  dataNascimento: '',
  sexo: '',
  tipoSanguineo: '',
  fatorRh: '',
  telefone: '',
  pesoEmQuilos: '',
  impedimentoDefinitivo: false,
  dataUltimaTatuagem: '',
}

const SELECIONE = { valor: '', rotulo: 'Selecione' }
const OPCOES_SEXO = [SELECIONE, { valor: 'MASCULINO', rotulo: 'Masculino' }, { valor: 'FEMININO', rotulo: 'Feminino' }]
const OPCOES_TIPO = [SELECIONE, ...TIPOS_SANGUINEOS.map((tipo) => ({ valor: tipo, rotulo: tipo }))]
const OPCOES_RH = [SELECIONE, { valor: 'POSITIVO', rotulo: 'Positivo (+)' }, { valor: 'NEGATIVO', rotulo: 'Negativo (-)' }]

// Mesmas regras do backend (DoadorRequest).
function validar(valores) {
  const erros = {}
  const hoje = hojeIso()
  const telefone = apenasDigitos(valores.telefone)
  if (!valores.nome.trim()) erros.nome = 'Nome é obrigatório'
  if (!valores.cpf) erros.cpf = 'CPF é obrigatório'
  if (valores.cpf && !cpfValido(valores.cpf)) erros.cpf = 'CPF inválido'
  if (!valores.dataNascimento) erros.dataNascimento = 'Data de nascimento é obrigatória'
  if (valores.dataNascimento >= hoje) erros.dataNascimento = 'Data de nascimento deve estar no passado'
  if (!valores.sexo) erros.sexo = 'Sexo é obrigatório'
  if (!valores.tipoSanguineo) erros.tipoSanguineo = 'Tipo sanguíneo é obrigatório'
  if (!valores.fatorRh) erros.fatorRh = 'Fator Rh é obrigatório'
  if (telefone && (telefone.length < 10 || telefone.length > 11)) erros.telefone = 'Telefone deve ter 10 ou 11 dígitos'
  if (valores.pesoEmQuilos === '') erros.pesoEmQuilos = 'Peso é obrigatório'
  if (valores.pesoEmQuilos !== '' && Number(valores.pesoEmQuilos) <= 0) erros.pesoEmQuilos = 'Peso deve ser maior que zero'
  if (valores.dataUltimaTatuagem > hoje) erros.dataUltimaTatuagem = 'Data da tatuagem não pode ser futura'
  return erros
}

function paraFormulario(doador) {
  return {
    nome: doador.nome,
    cpf: mascararCpf(doador.cpf),
    dataNascimento: doador.dataNascimento,
    sexo: doador.sexo,
    tipoSanguineo: doador.tipoSanguineo,
    fatorRh: doador.fatorRh,
    telefone: mascararTelefone(doador.telefone ?? ''),
    pesoEmQuilos: String(doador.fichaClinica?.pesoEmQuilos ?? ''),
    impedimentoDefinitivo: Boolean(doador.fichaClinica?.impedimentoDefinitivo),
    dataUltimaTatuagem: doador.fichaClinica?.dataUltimaTatuagem ?? '',
  }
}

function paraPedido(valores) {
  return {
    nome: valores.nome.trim(),
    cpf: apenasDigitos(valores.cpf),
    dataNascimento: valores.dataNascimento,
    sexo: valores.sexo,
    tipoSanguineo: valores.tipoSanguineo,
    fatorRh: valores.fatorRh,
    telefone: apenasDigitos(valores.telefone) || null,
    fichaClinica: {
      pesoEmQuilos: Number(valores.pesoEmQuilos),
      impedimentoDefinitivo: valores.impedimentoDefinitivo,
      dataUltimaTatuagem: valores.dataUltimaTatuagem || null,
    },
  }
}

export default function FormularioDoador() {
  const { id } = useParams()
  const navegar = useNavigate()
  const [valores, setValores] = useState(VAZIO)
  const [erros, setErros] = useState({})
  const [carregando, setCarregando] = useState(Boolean(id))
  const [enviando, setEnviando] = useState(false)
  const [aviso, setAviso] = useState('')
  const fecharAviso = useCallback(() => setAviso(''), [])

  useEffect(() => {
    if (!id) return
    buscar(id)
      .then((doador) => setValores(paraFormulario(doador)))
      .catch((falha) => setAviso(mensagemDoErro(falha)))
      .finally(() => setCarregando(false))
  }, [id])

  // Leva o foco ao primeiro campo com erro, para leitores de tela e teclado.
  useEffect(() => {
    document.querySelector('[aria-invalid="true"]')?.focus()
  }, [erros])

  const alterar = (campo, transformar = (texto) => texto) => (evento) =>
    setValores((atual) => ({ ...atual, [campo]: transformar(evento.target.value) }))

  async function enviar(evento) {
    evento.preventDefault()
    const encontrados = validar(valores)
    setErros(encontrados)
    if (Object.keys(encontrados).length > 0) return

    setEnviando(true)
    try {
      const doador = id ? await atualizar(id, paraPedido(valores)) : await criar(paraPedido(valores))
      navegar(`/doadores/${doador.id}`, {
        state: { aviso: id ? 'Doador atualizado.' : 'Doador cadastrado.' },
      })
    } catch (falha) {
      const doServidor = errosDeCampo(falha)
      setErros(doServidor)
      if (Object.keys(doServidor).length === 0) setAviso(mensagemDoErro(falha))
      setEnviando(false)
    }
  }

  if (carregando) return <Carregando />

  return (
    <>
      <h1>{id ? 'Editar doador' : 'Novo doador'}</h1>
      <form onSubmit={enviar} noValidate>
        <fieldset className="secao">
          <legend>Dados pessoais</legend>
          <div className="grade">
            <Campo rotulo="Nome completo" value={valores.nome} onChange={alterar('nome')} erro={erros.nome} autoComplete="off" />
            <Campo
              rotulo="CPF"
              value={valores.cpf}
              onChange={alterar('cpf', mascararCpf)}
              erro={erros.cpf}
              inputMode="numeric"
              placeholder="000.000.000-00"
            />
            <Campo
              rotulo="Data de nascimento"
              type="date"
              value={valores.dataNascimento}
              onChange={alterar('dataNascimento')}
              erro={erros.dataNascimento}
            />
            <Campo rotulo="Sexo" opcoes={OPCOES_SEXO} value={valores.sexo} onChange={alterar('sexo')} erro={erros.sexo} />
            <Campo
              rotulo="Tipo sanguíneo"
              opcoes={OPCOES_TIPO}
              value={valores.tipoSanguineo}
              onChange={alterar('tipoSanguineo')}
              erro={erros.tipoSanguineo}
            />
            <Campo rotulo="Fator Rh" opcoes={OPCOES_RH} value={valores.fatorRh} onChange={alterar('fatorRh')} erro={erros.fatorRh} />
            <Campo
              rotulo="Telefone (opcional)"
              value={valores.telefone}
              onChange={alterar('telefone', mascararTelefone)}
              erro={erros.telefone}
              inputMode="tel"
              placeholder="(00) 00000-0000"
            />
          </div>
        </fieldset>

        <fieldset className="secao">
          <legend>Ficha clínica</legend>
          <div className="grade">
            <Campo
              rotulo="Peso (kg)"
              type="number"
              step="0.1"
              min="0"
              value={valores.pesoEmQuilos}
              onChange={alterar('pesoEmQuilos')}
              erro={erros.pesoEmQuilos}
            />
            <Campo
              rotulo="Data da última tatuagem (opcional)"
              type="date"
              value={valores.dataUltimaTatuagem}
              onChange={alterar('dataUltimaTatuagem')}
              erro={erros.dataUltimaTatuagem}
            />
          </div>
          <label className="caixa">
            <input
              type="checkbox"
              checked={valores.impedimentoDefinitivo}
              onChange={(evento) => setValores((atual) => ({ ...atual, impedimentoDefinitivo: evento.target.checked }))}
            />
            Possui impedimento definitivo para doar
          </label>
        </fieldset>

        <div className="formulario__acoes">
          <Botao type="submit" carregando={enviando}>
            Salvar
          </Botao>
          <Link to={id ? `/doadores/${id}` : '/doadores'} className="botao botao--secundario">
            Cancelar
          </Link>
        </div>
      </form>
      {aviso && <Aviso tipo="erro" mensagem={aviso} aoFechar={fecharAviso} />}
    </>
  )
}
