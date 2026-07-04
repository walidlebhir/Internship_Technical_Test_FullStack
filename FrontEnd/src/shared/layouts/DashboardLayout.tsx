import { useEffect, useRef, useState } from 'react'
import { NavLink, Outlet, useLocation } from 'react-router-dom'
import { cn } from '@/shared/lib/cn'
import { ROUTES } from '@/shared/constants/routes'
import { useMediaQuery } from '@/shared/hooks'
import {
  LayoutDashboard,
  Flag,
  Globe,
  GitBranch,
  TestTube,
  ClipboardList,
  Settings,
  Menu,
  X,
  Moon,
  Sun,
} from 'lucide-react'
import { useThemeStore } from '@/app/store'

const navItems = [
  { to: ROUTES.DASHBOARD, label: 'Dashboard', icon: LayoutDashboard },
  { to: ROUTES.DOMAINS, label: 'Domains', icon: Globe },
  { to: ROUTES.FEATURES, label: 'Features', icon: Flag },
  { to: ROUTES.STRATEGIES, label: 'Strategies', icon: GitBranch },
  { to: ROUTES.EVALUATION, label: 'Evaluation', icon: TestTube },
  { to: ROUTES.AUDIT, label: 'Audit Log', icon: ClipboardList },
  { to: ROUTES.SETTINGS, label: 'Settings', icon: Settings },
]

export function DashboardLayout() {
  const [sidebarOpen, setSidebarOpen] = useState(true)
  const location = useLocation()
  const { theme, setTheme } = useThemeStore()
  const isDesktop = useMediaQuery('(min-width: 1024px)')
  const mainRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    mainRef.current?.scrollTo({ top: 0, behavior: 'instant' })
  }, [location.pathname])

  const toggleTheme = () => {
    setTheme(theme === 'dark' ? 'light' : 'dark')
  }

  return (
    <div className="flex h-screen overflow-hidden bg-[var(--color-bg-secondary)]">
      {sidebarOpen && !isDesktop && (
        <div
          className="fixed inset-0 z-40 bg-black/50"
          onClick={() => setSidebarOpen(false)}
        />
      )}

      <aside
        className={cn(
          'flex flex-col border-r border-[var(--color-border)] bg-[var(--color-bg-elevated)] transition-all duration-300',
          isDesktop
            ? 'static'
            : 'fixed inset-y-0 left-0 z-50',
          sidebarOpen
            ? 'w-64'
            : 'w-0 overflow-hidden',
        )}
      >
        <div className="flex h-16 shrink-0 items-center justify-between border-b border-[var(--color-border)] px-6">
          <div className="flex items-center gap-3">
            <div className="flex size-8 items-center justify-center rounded-lg bg-[var(--color-accent)]">
              <Flag className="size-4 text-white" />
            </div>
            <span className="text-lg font-bold text-[var(--color-text-primary)]">
              Feature Flags
            </span>
          </div>
          {!isDesktop && (
            <button
              onClick={() => setSidebarOpen(false)}
              className="rounded-lg p-1.5 text-[var(--color-text-tertiary)] hover:bg-[var(--color-bg-tertiary)]"
            >
              <X className="size-5" />
            </button>
          )}
        </div>

        <nav className="flex-1 space-y-1 overflow-y-auto p-4">
          {navItems.map((item) => {
            const isActive = location.pathname === item.to ||
              (item.to !== '/' && location.pathname.startsWith(item.to))
            return (
              <NavLink
                key={item.to}
                to={item.to}
                onClick={() => { if (!isDesktop) setSidebarOpen(false) }}
                className={cn(
                  'flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium transition-colors duration-150',
                  isActive
                    ? 'bg-[var(--color-accent-light)] text-[var(--color-accent)]'
                    : 'text-[var(--color-text-secondary)] hover:bg-[var(--color-bg-tertiary)] hover:text-[var(--color-text-primary)]',
                )}
              >
                <item.icon className="size-5 shrink-0" />
                <span>{item.label}</span>
              </NavLink>
            )
          })}
        </nav>

        <div className="shrink-0 border-t border-[var(--color-border)] p-4">
          <div className="flex items-center gap-3 rounded-lg px-3 py-2 text-sm text-[var(--color-text-secondary)]">
            <div className="flex size-8 items-center justify-center rounded-full bg-[var(--color-accent-light)] text-[var(--color-accent)] font-semibold">
              A
            </div>
            <div className="flex-1 truncate">
              <p className="font-medium text-[var(--color-text-primary)]">Admin</p>
              <p className="text-xs">admin@platform.com</p>
            </div>
          </div>
        </div>
      </aside>

      <div className="flex flex-1 flex-col min-w-0">
        <header className="flex h-16 shrink-0 items-center justify-between border-b border-[var(--color-border)] bg-[var(--color-bg-elevated)] px-4 lg:px-6">
          <div className="flex items-center gap-3">
            {!isDesktop && (
              <button
                onClick={() => setSidebarOpen(true)}
                className="rounded-lg p-2 text-[var(--color-text-secondary)] hover:bg-[var(--color-bg-tertiary)] transition-colors"
              >
                <Menu className="size-5" />
              </button>
            )}
          </div>

          <div className="flex items-center gap-2">
            <button
              onClick={toggleTheme}
              className="rounded-lg p-2 text-[var(--color-text-secondary)] hover:bg-[var(--color-bg-tertiary)] transition-colors"
              title="Toggle theme"
            >
              {theme === 'dark' ? <Sun className="size-5" /> : <Moon className="size-5" />}
            </button>
          </div>
        </header>

        <main ref={mainRef} className="flex-1 overflow-y-auto p-6">
          <Outlet />
        </main>
      </div>
    </div>
  )
}
