import cliente from './cliente'

export const entrar = (usuario, senha) =>
  cliente.post('/auth/login', { usuario, senha }).then((resposta) => resposta.data)
