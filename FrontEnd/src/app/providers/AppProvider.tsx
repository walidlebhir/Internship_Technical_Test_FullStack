import type { ReactNode } from 'react'
import { QueryProvider } from '@/shared/providers/QueryProvider'
import { ThemeProvider } from '@/shared/providers/ThemeProvider'
import { I18nProvider } from '@/shared/providers/I18nProvider'

interface AppProviderProps {
  children: ReactNode
}

export function AppProvider({ children }: AppProviderProps) {
  return (
    <QueryProvider>
      <ThemeProvider>
        <I18nProvider>
          {children}
        </I18nProvider>
      </ThemeProvider>
    </QueryProvider>
  )
}
