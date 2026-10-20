import { useEffect } from 'react'

const SEGUNDOS_VISIVEL = 6

// tipo: 'sucesso' | 'erro'. Some sozinho; quem usa decide quando renderizar.
export default function Aviso({ tipo = 'sucesso', mensagem, aoFechar }) {
  useEffect(() => {
    const temporizador = setTimeout(aoFechar, SEGUNDOS_VISIVEL * 1000)
    return () => clearTimeout(temporizador)
  }, [mensagem, aoFechar])

  return (
    <div className={`aviso aviso--${tipo}`} role={tipo === 'erro' ? 'alert' : 'status'}>
      <span>{mensagem}</span>
      <button type="button" className="aviso__fechar" onClick={aoFechar} aria-label="Fechar aviso">
        ×
      </button>
    </div>
  )
}
