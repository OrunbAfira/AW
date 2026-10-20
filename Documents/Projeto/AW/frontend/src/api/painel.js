import cliente from './cliente'

export const obter = () => cliente.get('/painel').then((resposta) => resposta.data)
