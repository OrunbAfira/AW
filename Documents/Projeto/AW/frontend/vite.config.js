import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// O navegador fala só com o Vite; /api é repassado ao backend, por isso o backend não precisa de CORS.
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': 'http://localhost:8080',
    },
  },
})
