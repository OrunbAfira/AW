import { useState } from 'react'
import { Navigate, useNavigate } from 'react-router-dom'
import { entrar } from '../api/auth'
import { mensagemDoErro } from '../api/cliente'
import Botao from '../componentes/Botao'
import Campo from '../componentes/Campo'
import { lerSessao, salvarSessao } from '../sessao'

export default function Login() {
  const navegar = useNavigate()
  const [usuario, setUsuario] = useState('')
  const [senha, setSenha] = useState('')
  const [erro, setErro] = useState('')
  const [enviando, setEnviando] = useState(false)

  if (lerSessao().token) return <Navigate to="/" replace />

  async function enviar(evento) {
    evento.preventDefault()
    setErro('')
    setEnviando(true)
    try {
      const { token, perfil } = await entrar(usuario, senha)
      salvarSessao({ token, perfil, usuario })
      navegar('/', { replace: true })
    } catch (falha) {
      setErro(mensagemDoErro(falha))
      setEnviando(false)
    }
  }

  return (
    <main className="login">
      <form className="login__cartao" onSubmit={enviar}>
        <h1 className="login__marca">HemoVida</h1>
        <p className="login__subtitulo">Entre para acessar o sistema.</p>
        {erro && (
          <p className="login__erro" role="alert">
            {erro}
          </p>
        )}
        <Campo
          rotulo="Usuário"
          value={usuario}
          onChange={(evento) => setUsuario(evento.target.value)}
          autoComplete="username"
          autoFocus
          required
        />
        <Campo
          rotulo="Senha"
          type="password"
          value={senha}
          onChange={(evento) => setSenha(evento.target.value)}
          autoComplete="current-password"
          required
        />
        <Botao type="submit" carregando={enviando}>
          Entrar
        </Botao>
      </form>
    </main>
  )
}
