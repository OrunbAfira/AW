export default function Carregando({ texto = 'Carregando…' }) {
  return (
    <div className="carregando" role="status">
      <span className="carregando__roda" aria-hidden="true" />
      {texto}
    </div>
  )
}
