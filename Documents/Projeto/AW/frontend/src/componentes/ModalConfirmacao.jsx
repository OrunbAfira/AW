import Botao from './Botao'
import Modal from './Modal'

export default function ModalConfirmacao({
  aberto,
  titulo,
  mensagem,
  textoConfirmar = 'Confirmar',
  aoConfirmar,
  aoCancelar,
}) {
  return (
    <Modal aberto={aberto} titulo={titulo} aoFechar={aoCancelar}>
      <p>{mensagem}</p>
      <div className="modal__acoes">
        <Botao variante="secundario" onClick={aoCancelar}>
          Cancelar
        </Botao>
        <Botao onClick={aoConfirmar}>{textoConfirmar}</Botao>
      </div>
    </Modal>
  )
}
