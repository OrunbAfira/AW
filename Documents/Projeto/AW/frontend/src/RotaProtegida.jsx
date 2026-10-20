import { Navigate } from 'react-router-dom'
import { lerSessao } from './sessao'

export default function RotaProtegida({ children }) {
  return lerSessao().token ? children : <Navigate to="/login" replace />
}
