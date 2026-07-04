import { Card, CardContent, CardHeader, CardTitle } from '@/shared/components'
import { Flag, Globe, GitBranch, Activity, ArrowUpRight, Clock } from 'lucide-react'

const stats = [
  { label: 'Total Features', value: '--', icon: Flag },
  { label: 'Active Domains', value: '--', icon: Globe },
  { label: 'Strategies', value: '--', icon: GitBranch },
  { label: 'Evaluations', value: '--', icon: Activity },
]

export function DashboardPage() {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-[var(--color-text-primary)]">Dashboard</h1>
        <p className="mt-1 text-sm text-[var(--color-text-secondary)]">
          Overview of your feature flag platform
        </p>
      </div>

      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        {stats.map((stat) => (
          <Card key={stat.label}>
            <CardContent className="p-6">
              <div className="flex size-12 items-center justify-center rounded-xl bg-[var(--color-accent-light)]">
                <stat.icon className="size-6 text-[var(--color-accent)]" />
              </div>
              <p className="mt-4 text-2xl font-bold text-[var(--color-text-primary)]">{stat.value}</p>
              <p className="mt-1 text-sm text-[var(--color-text-secondary)]">{stat.label}</p>
            </CardContent>
          </Card>
        ))}
      </div>

      <div className="grid gap-6 lg:grid-cols-2">
        <Card>
          <CardHeader>
            <CardTitle>Recent Activity</CardTitle>
          </CardHeader>
          <CardContent className="p-0">
            <div className="flex flex-col items-center justify-center py-12 text-sm text-[var(--color-text-tertiary)]">
              <Clock className="size-8 mb-2" />
              <p>No recent activity</p>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Quick Actions</CardTitle>
          </CardHeader>
          <CardContent className="space-y-3">
            <button className="flex w-full items-center gap-3 rounded-lg border border-[var(--color-border)] bg-[var(--color-bg-primary)] p-4 text-left transition-colors hover:bg-[var(--color-bg-tertiary)]">
              <div className="flex size-10 items-center justify-center rounded-lg bg-[var(--color-accent-light)]">
                <Flag className="size-5 text-[var(--color-accent)]" />
              </div>
              <div className="flex-1">
                <p className="text-sm font-medium text-[var(--color-text-primary)]">Create Feature Flag</p>
                <p className="text-xs text-[var(--color-text-secondary)]">Add a new feature flag to any domain</p>
              </div>
            </button>
            <button className="flex w-full items-center gap-3 rounded-lg border border-[var(--color-border)] bg-[var(--color-bg-primary)] p-4 text-left transition-colors hover:bg-[var(--color-bg-tertiary)]">
              <div className="flex size-10 items-center justify-center rounded-lg bg-[var(--color-info-light)]">
                <Globe className="size-5 text-[var(--color-info)]" />
              </div>
              <div className="flex-1">
                <p className="text-sm font-medium text-[var(--color-text-primary)]">New Domain</p>
                <p className="text-xs text-[var(--color-text-secondary)]">Create a new domain environment</p>
              </div>
            </button>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}
