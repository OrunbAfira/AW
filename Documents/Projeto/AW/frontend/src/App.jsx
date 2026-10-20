import { Navigate, Route, Routes } from 'react-router-dom'
import RotaProtegida from './RotaProtegida'
import DetalheDoador from './paginas/DetalheDoador'
import Doadores from './paginas/Doadores'
import FormularioDoador from './paginas/FormularioDoador'
import Layout from './paginas/Layout'
import Login from './paginas/Login'
import Painel from './paginas/Painel'

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route
        element={
          <RotaProtegida>
            <Layout />
          </RotaProtegida>
        }
      >
        <Route index element={<Painel />} />
        <Route path="doadores" element={<Doadores />} />
        <Route path="doadores/novo" element={<FormularioDoador />} />
        <Route path="doadores/:id" element={<DetalheDoador />} />
        <Route path="doadores/:id/editar" element={<FormularioDoador />} />
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}
