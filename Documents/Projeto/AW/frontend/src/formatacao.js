export const TIPOS_SANGUINEOS = ['A', 'B', 'AB', 'O']

export const apenasDigitos = (texto = '') => texto.replace(/\D/g, '')

// "2026-10-08" -> "08/10/2026". Sem passar por Date, para não deslocar o dia por fuso.
export function formatarData(iso) {
  if (!iso) return '—'
  const [ano, mes, dia] = iso.split('-')
  return `${dia}/${mes}/${ano}`
}

export function formatarDiaMes(iso) {
  const [, mes, dia] = iso.split('-')
  return `${dia}/${mes}`
}

export function hojeIso() {
  const agora = new Date()
  const dois = (numero) => String(numero).padStart(2, '0')
  return `${agora.getFullYear()}-${dois(agora.getMonth() + 1)}-${dois(agora.getDate())}`
}

export function mascararCpf(texto) {
  const d = apenasDigitos(texto).slice(0, 11)
  let resultado = d.slice(0, 3)
  if (d.length > 3) resultado += `.${d.slice(3, 6)}`
  if (d.length > 6) resultado += `.${d.slice(6, 9)}`
  if (d.length > 9) resultado += `-${d.slice(9)}`
  return resultado
}

export function mascararTelefone(texto) {
  const d = apenasDigitos(texto).slice(0, 11)
  if (d.length <= 2) return d
  const resto = d.slice(2)
  const corte = d.length > 10 ? 5 : 4
  const parte = resto.length > corte ? `${resto.slice(0, corte)}-${resto.slice(corte)}` : resto
  return `(${d.slice(0, 2)}) ${parte}`
}

// Mesma regra do backend (dígitos verificadores).
export function cpfValido(texto) {
  const d = apenasDigitos(texto)
  if (d.length !== 11 || new Set(d).size === 1) return false
  const digito = (quantidade) => {
    let soma = 0
    for (let i = 0; i < quantidade; i++) soma += Number(d[i]) * (quantidade + 1 - i)
    const resto = (soma * 10) % 11
    return resto === 10 ? 0 : resto
  }
  return digito(9) === Number(d[9]) && digito(10) === Number(d[10])
}

export const tipoCompleto = (tipo, fator) => `${tipo}${fator === 'POSITIVO' ? '+' : '-'}`

export const formatarNumero = (numero) => Number(numero).toLocaleString('pt-BR')
