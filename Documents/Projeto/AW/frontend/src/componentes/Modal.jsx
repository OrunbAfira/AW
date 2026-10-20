import { useEffect, useId, useRef } from 'react'

// Usa <dialog>: o navegador cuida do foco preso no modal. O conteúdo continua montado
// quando fechado, então o que foi digitado não se perde ao fechar e reabrir.
export default function Modal({ aberto, titulo, aoFechar, children }) {
  const dialogo = useRef(null)
  const idTitulo = useId()

  useEffect(() => {
    const elemento = dialogo.current
    if (aberto && !elemento.open) elemento.showModal()
    if (!aberto && elemento.open) elemento.close()
  }, [aberto])

  return (
    <dialog
      ref={dialogo}
      className="modal"
      aria-labelledby={idTitulo}
      onCancel={(evento) => {
        evento.preventDefault()
        aoFechar()
      }}
    >
      <h2 id={idTitulo} className="modal__titulo">
        {titulo}
      </h2>
      {children}
    </dialog>
  )
}
