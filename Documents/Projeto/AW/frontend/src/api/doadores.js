import cliente from './cliente'

const dados = (resposta) => resposta.data

export const listar = ({ busca, tipoSanguineo }) =>
  cliente
    .get('/doadores', { params: { busca: busca || undefined, tipoSanguineo: tipoSanguineo || undefined } })
    .then(dados)

export const buscar = (id) => cliente.get(`/doadores/${id}`).then(dados)

export const criar = (pedido) => cliente.post('/doadores', pedido).then(dados)

export const atualizar = (id, pedido) => cliente.put(`/doadores/${id}`, pedido).then(dados)

export const excluir = (id) => cliente.delete(`/doadores/${id}`)

export const aptidao = (id) => cliente.get(`/doadores/${id}/aptidao`).then(dados)
