import cliente from './cliente'

export const listarDoDoador = (idDoador) =>
  cliente.get(`/doadores/${idDoador}/doacoes`).then((resposta) => resposta.data)

export const registrar = (idDoador, pedido) =>
  cliente.post(`/doadores/${idDoador}/doacoes`, pedido).then((resposta) => resposta.data)

export const buscar = (id) => cliente.get(`/doacoes/${id}`).then((resposta) => resposta.data)
