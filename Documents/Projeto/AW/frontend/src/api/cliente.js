import axios from 'axios'
import { lerSessao, limparSessao } from '../sessao'

const cliente = axios.create({ baseURL: '/api/v1' })

cliente.interceptors.request.use((requisicao) => {
  const { token } = lerSessao()
  if (token) requisicao.headers.Authorization = `Bearer ${token}`
  return requisicao
})

cliente.interceptors.response.use(
  (resposta) => resposta,
  (erro) => {
    // No login, o 401 é "usuário ou senha inválidos" e a própria tela mostra a mensagem.
    if (erro.response?.status === 401 && erro.config?.url !== '/auth/login') {
      limparSessao()
      window.location.assign('/login')
    }
    return Promise.reject(erro)
  },
)

export function mensagemDoErro(erro) {
  return erro.response?.data?.mensagem ?? 'Não foi possível conectar ao servidor'
}

// Erros de validação por campo: { "fichaClinica.pesoEmQuilos": "msg" } vira { pesoEmQuilos: "msg" }.
export function errosDeCampo(erro) {
  const campos = erro.response?.data?.campos ?? {}
  return Object.fromEntries(Object.entries(campos).map(([chave, mensagem]) => [chave.split('.').pop(), mensagem]))
}

export default cliente
