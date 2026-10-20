export default function Botao({ variante = 'primario', carregando = false, children, disabled, ...resto }) {
  return (
    <button
      type="button"
      className={`botao botao--${variante}`}
      disabled={disabled || carregando}
      aria-busy={carregando || undefined}
      {...resto}
    >
      {carregando ? 'Aguarde…' : children}
    </button>
  )
}
