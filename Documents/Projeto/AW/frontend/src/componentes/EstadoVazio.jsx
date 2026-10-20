export default function EstadoVazio({ titulo, descricao, children }) {
  return (
    <div className="estado-vazio">
      <h2 className="estado-vazio__titulo">{titulo}</h2>
      {descricao && <p>{descricao}</p>}
      {children}
    </div>
  )
}
