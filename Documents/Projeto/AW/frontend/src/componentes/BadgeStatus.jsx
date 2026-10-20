import { formatarDiaMes } from '../formatacao'

// Cor + texto, para não depender só da cor. "aptoAPartirDe" (aaaa-mm-dd) vale para inapto temporário.
export default function BadgeStatus({ status, aptoAPartirDe }) {
  if (status === 'APTO') return <span className="badge badge--verde">Apto</span>
  if (status === 'INAPTO_TEMPORARIO' && aptoAPartirDe) {
    return <span className="badge badge--amarelo">Apto a partir de {formatarDiaMes(aptoAPartirDe)}</span>
  }
  return <span className="badge badge--vermelho">Inapto</span>
}
