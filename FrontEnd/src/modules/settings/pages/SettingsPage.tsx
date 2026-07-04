import { Card, CardContent } from '@/shared/components'

export function SettingsPage() {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-[var(--color-text-primary)]">Settings</h1>
        <p className="mt-1 text-sm text-[var(--color-text-secondary)]">Platform settings</p>
      </div>
      <Card>
        <CardContent className="p-6 text-sm text-[var(--color-text-secondary)]">
          Settings management coming soon.
        </CardContent>
      </Card>
    </div>
  )
}
