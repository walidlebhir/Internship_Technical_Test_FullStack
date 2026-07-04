import { Card, CardContent, CardHeader, CardTitle } from '@/shared/components'
import { ArrowLeft, GitBranch, Loader2 } from 'lucide-react'
import { useNavigate, useParams } from 'react-router-dom'
import { useStrategy } from '../hooks'

export function StrategyDetailPage() {
  const navigate = useNavigate()
  const { strategyId } = useParams<{ strategyId: string }>()
  const { data: strategy, isLoading } = useStrategy(Number(strategyId))

  return (
    <div className="space-y-6">
      <button
        onClick={() => navigate('/strategies')}
        className="inline-flex items-center gap-1 text-sm text-[var(--color-text-secondary)] hover:text-[var(--color-text-primary)] transition-colors"
      >
        <ArrowLeft className="size-4" />
        Back to Strategies
      </button>

      {isLoading ? (
        <div className="flex items-center justify-center py-16">
          <Loader2 className="size-8 animate-spin text-[var(--color-text-tertiary)]" />
        </div>
      ) : strategy ? (
        <Card>
          <CardHeader>
            <div className="flex items-center gap-3">
              <div className="flex size-10 items-center justify-center rounded-lg bg-[var(--color-accent-light)]">
                <GitBranch className="size-5 text-[var(--color-accent)]" />
              </div>
              <div>
                <CardTitle>{strategy.type}</CardTitle>
                <p className="text-sm text-[var(--color-text-secondary)]">Strategy #{strategy.id}</p>
              </div>
            </div>
          </CardHeader>
          <CardContent className="space-y-3">
            <div className="flex justify-between border-b border-[var(--color-border)] pb-2">
              <span className="text-sm text-[var(--color-text-secondary)]">ID</span>
              <span className="text-sm font-mono text-[var(--color-text-primary)]">{strategy.id}</span>
            </div>
            <div className="flex justify-between border-b border-[var(--color-border)] pb-2">
              <span className="text-sm text-[var(--color-text-secondary)]">Type</span>
              <span className="text-sm font-medium text-[var(--color-text-primary)]">{strategy.type}</span>
            </div>
            <div className="flex justify-between border-b border-[var(--color-border)] pb-2">
              <span className="text-sm text-[var(--color-text-secondary)]">Active</span>
              <span className={`text-sm font-medium ${
                strategy.active ? 'text-[var(--color-success)]' : 'text-[var(--color-text-tertiary)]'
              }`}>
                {strategy.active ? 'Yes' : 'No'}
              </span>
            </div>
            <div className="flex justify-between border-b border-[var(--color-border)] pb-2">
              <span className="text-sm text-[var(--color-text-secondary)]">Feature ID</span>
              <span className="text-sm font-mono text-[var(--color-text-primary)]">{strategy.featureId}</span>
            </div>
            <div className="pt-2">
              <p className="text-sm font-medium text-[var(--color-text-secondary)] mb-2">Config</p>
              <pre className="rounded-lg bg-[var(--color-bg-tertiary)] p-3 text-sm font-mono text-[var(--color-text-primary)] overflow-x-auto">
                {JSON.stringify(JSON.parse(strategy.config), null, 2)}
              </pre>
            </div>
          </CardContent>
        </Card>
      ) : (
        <Card>
          <CardContent className="py-12 text-center text-sm text-[var(--color-text-tertiary)]">Strategy not found</CardContent>
        </Card>
      )}
    </div>
  )
}
