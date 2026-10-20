import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import Botao from '../componentes/Botao'
import { lerSessao, limparSessao } from '../sessao'

const classeDoLink = ({ isActive }) => `lateral__link${isActive ? ' ativo' : ''}`

export default function Layout() {
  const navegar = useNavigate()
  const { usuario, perfil } = lerSessao()

  function sair() {
    limparSessao()
    navegar('/login', { replace: true })
  }

  return (
    <div className="layout">
      <aside className="lateral">
        <p className="lateral__marca">HemoVida</p>
        <nav className="lateral__menu" aria-label="Principal">
          <NavLink to="/" end className={classeDoLink}>
            Painel
          </NavLink>
          <NavLink to="/doadores" className={classeDoLink}>
            Doadores
          </NavLink>
        </nav>
        <div className="lateral__usuario">
          <div>
            <div className="lateral__nome">{usuario}</div>
            <div className="lateral__perfil">{perfil}</div>
          </div>
          <Botao variante="secundario" onClick={sair}>
            Sair
          </Botao>
        </div>
      </aside>
      <main className="conteudo">
        <Outlet />
      </main>
    </div>
  )
}
