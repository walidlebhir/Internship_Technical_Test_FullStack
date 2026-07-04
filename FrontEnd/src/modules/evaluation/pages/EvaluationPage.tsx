import { useState } from 'react'
import { Button, Card, CardContent, CardHeader, CardTitle } from '@/shared/components'
import { TestTube, Play, CheckCircle2, XCircle, Loader2 } from 'lucide-react'
import { useEvaluateFlag } from '../hooks'
import { useFeatures } from '@/modules/features/hooks'
import { useDomains } from '@/modules/domains/hooks'

export function EvaluationPage() {
  const [featureKey, setFeatureKey] = useState('')
  const [userId, setUserId] = useState('')
  const [environment, setEnvironment] = useState('')
  const { data: features } = useFeatures()
  const { data: result, isPending, mutateAsync, reset } = useEvaluateFlag()

  const handleEvaluate = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!featureKey) return
    await mutateAsync({ featureKey, userId: userId || undefined, environment: environment || undefined })
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-[var(--color-text-primary)]">Evaluation</h1>
        <p className="mt-1 text-sm text-[var(--color-text-secondary)]">Test and evaluate feature flags in real-time</p>
      </div>

      <div className="grid gap-6 lg:grid-cols-2">
        <Card>
          <CardHeader>
            <CardTitle>Evaluate a Feature Flag</CardTitle>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleEvaluate} className="space-y-4">
              <div className="space-y-1.5">
                <label className="text-sm font-medium text-[var(--color-text-secondary)]">Feature Flag</label>
                <select
                  value={featureKey}
                  onChange={(e) => { setFeatureKey(e.target.value); reset() }}
                  className="h-10 w-full rounded-lg border border-[var(--color-border)] bg-[var(--color-bg-primary)] px-3 text-sm text-[var(--color-text-primary)] focus:outline-none focus:ring-2 focus:ring-[var(--color-accent)]"
                >
                  <option value="">Select a feature...</option>
                  {features?.map((f) => (
                    <option key={f.id} value={f.key}>{f.key}</option>
                  ))}
                </select>
              </div>
              <div className="space-y-1.5">
                <label className="text-sm font-medium text-[var(--color-text-secondary)]">User ID (optional)</label>
                <input
                  value={userId}
                  onChange={(e) => setUserId(e.target.value)}
                  placeholder="e.g. user_123"
                  className="h-10 w-full rounded-lg border border-[var(--color-border)] bg-[var(--color-bg-primary)] px-3 text-sm text-[var(--color-text-primary)] placeholder:text-[var(--color-text-tertiary)] focus:outline-none focus:ring-2 focus:ring-[var(--color-accent)]"
                />
              </div>
              <div className="space-y-1.5">
                <label className="text-sm font-medium text-[var(--color-text-secondary)]">Environment (optional)</label>
                <input
                  value={environment}
                  onChange={(e) => setEnvironment(e.target.value)}
                  placeholder="e.g. production"
                  className="h-10 w-full rounded-lg border border-[var(--color-border)] bg-[var(--color-bg-primary)] px-3 text-sm text-[var(--color-text-primary)] placeholder:text-[var(--color-text-tertiary)] focus:outline-none focus:ring-2 focus:ring-[var(--color-accent)]"
                />
              </div>
              <Button type="submit" leftIcon={<Play className="size-4" />} isLoading={isPending} disabled={!featureKey}>
                Evaluate
              </Button>
            </form>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Result</CardTitle>
          </CardHeader>
          <CardContent>
            {isPending ? (
              <div className="flex items-center justify-center py-12">
                <Loader2 className="size-8 animate-spin text-[var(--color-text-tertiary)]" />
              </div>
            ) : result ? (
              <div className="space-y-4">
                <div className={`flex items-center gap-3 rounded-xl border p-4 ${
                  result.enabled
                    ? 'border-[var(--color-success)] bg-[var(--color-success-light)]'
                    : 'border-[var(--color-error)] bg-[var(--color-error-light)]'
                }`}>
                  {result.enabled
                    ? <CheckCircle2 className="size-8 text-[var(--color-success)]" />
                    : <XCircle className="size-8 text-[var(--color-error)]" />
                  }
                  <div>
                    <p className="text-lg font-bold text-[var(--color-text-primary)]">
                      {result.enabled ? 'ENABLED' : 'DISABLED'}
                    </p>
                    <p className="text-sm text-[var(--color-text-secondary)]">
                      Feature <strong>{result.featureKey}</strong> is {result.enabled ? 'active' : 'inactive'}
                      {result.userId && ` for user ${result.userId}`}
                    </p>
                  </div>
                </div>
              </div>
            ) : (
              <div className="flex flex-col items-center justify-center py-12">
                <TestTube className="size-12 text-[var(--color-text-tertiary)]" />
                <p className="mt-4 text-sm text-[var(--color-text-secondary)]">Select a feature and click Evaluate</p>
              </div>
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  )
}
