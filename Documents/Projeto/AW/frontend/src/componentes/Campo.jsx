import { useId } from 'react'

// Com "opcoes" vira um <select>; sem, um <input>. "dica" é um texto de ajuda sob o campo.
export default function Campo({ rotulo, erro, dica, opcoes, ...entrada }) {
  const id = useId()
  const descricoes = [dica && `${id}-dica`, erro && `${id}-erro`].filter(Boolean).join(' ') || undefined
  const propriedades = {
    id,
    className: 'campo__entrada',
    'aria-invalid': erro ? 'true' : undefined,
    'aria-describedby': descricoes,
    ...entrada,
  }
  return (
    <div className="campo">
      <label className="campo__rotulo" htmlFor={id}>
        {rotulo}
      </label>
      {opcoes ? (
        <select {...propriedades}>
          {opcoes.map((opcao) => (
            <option key={opcao.valor} value={opcao.valor}>
              {opcao.rotulo}
            </option>
          ))}
        </select>
      ) : (
        <input {...propriedades} />
      )}
      {dica && (
        <span id={`${id}-dica`} className="campo__dica">
          {dica}
        </span>
      )}
      {erro && (
        <span id={`${id}-erro`} className="campo__erro">
          {erro}
        </span>
      )}
    </div>
  )
}
