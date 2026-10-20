@echo off
rem Abre o backend e o frontend em duas janelas. Veja o README.
if not exist "%~dp0backend\.env" (
  echo Falta backend\.env: copie backend\.env.example e preencha os valores. Veja o README.
  pause
  exit /b 1
)
start "HemoVida - Backend" /d "%~dp0backend" cmd /k .\mvnw.cmd spring-boot:run
start "HemoVida - Frontend" /d "%~dp0frontend" cmd /k "npm install && npm run dev"
