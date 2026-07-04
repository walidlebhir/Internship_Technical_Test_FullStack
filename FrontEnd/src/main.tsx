import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { AppProvider } from '@/app/providers'
import { AppRouter } from '@/app/router'
import { Toaster } from 'sonner'
import '@/styles/global.css'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <AppProvider>
      <AppRouter />
      <Toaster
        position="top-right"
        richColors
        closeButton
      />
    </AppProvider>
  </StrictMode>,
)
