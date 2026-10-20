// Sessão guardada em sessionStorage: some ao fechar a aba.
const CHAVE = 'hemovida.sessao'

export function salvarSessao({ token, perfil, usuario }) {
  sessionStorage.setItem(CHAVE, JSON.stringify({ token, perfil, usuario }))
}

export function lerSessao() {
  try {
    return JSON.parse(sessionStorage.getItem(CHAVE)) ?? {}
  } catch {
    return {}
  }
}

export function limparSessao() {
  sessionStorage.removeItem(CHAVE)
}
