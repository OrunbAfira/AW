#!/bin/sh
# Abre o backend e o frontend em dois terminais (macOS ou Linux com terminal gráfico).
# Sem terminal gráfico, roda os dois neste terminal. Veja o README.
raiz=$(cd "$(dirname "$0")" && pwd)

if [ ! -f "$raiz/backend/.env" ]; then
  echo "Falta backend/.env: copie backend/.env.example e preencha os valores. Veja o README." >&2
  exit 1
fi

backend="cd '$raiz/backend' && sh mvnw spring-boot:run"
frontend="cd '$raiz/frontend' && npm install && npm run dev"

abrir() {
  if command -v gnome-terminal >/dev/null 2>&1; then
    gnome-terminal -- sh -c "$1; exec sh"
  elif command -v konsole >/dev/null 2>&1; then
    konsole -e sh -c "$1; exec sh" &
  elif command -v xterm >/dev/null 2>&1; then
    xterm -e sh -c "$1; exec sh" &
  else
    return 1
  fi
}

if [ "$(uname)" = "Darwin" ]; then
  osascript -e "tell application \"Terminal\" to do script \"$backend\"" \
            -e "tell application \"Terminal\" to do script \"$frontend\"" \
            -e 'tell application "Terminal" to activate' >/dev/null
  exit 0
fi

if abrir "$backend"; then
  abrir "$frontend"
  exit 0
fi

echo "Nenhum terminal gráfico encontrado; rodando os dois neste terminal (Ctrl+C encerra ambos)."
trap 'kill 0' INT TERM EXIT
sh -c "$backend" &
sh -c "$frontend"
