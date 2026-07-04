import { Outlet } from 'react-router-dom'
import { Flag } from 'lucide-react'

export function AuthLayout() {
  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-[var(--color-bg-secondary)] px-4">
      <div className="mb-8 flex items-center gap-3">
        <div className="flex size-10 items-center justify-center rounded-xl bg-[var(--color-accent)] shadow-lg">
          <Flag className="size-5 text-white" />
        </div>
        <span className="text-2xl font-bold text-[var(--color-text-primary)]">
          Feature Flags
        </span>
      </div>

      <div className="w-full max-w-md rounded-xl border border-[var(--color-border)] bg-[var(--color-bg-elevated)] p-8 shadow-lg">
        <Outlet />
      </div>

      <p className="mt-8 text-sm text-[var(--color-text-tertiary)]">
        &copy; {new Date().getFullYear()} Feature Flag Platform. All rights reserved.
      </p>
    </div>
  )
}
