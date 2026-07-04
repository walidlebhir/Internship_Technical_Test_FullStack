import { Card, CardContent, CardHeader, CardTitle } from '@/shared/components'
import { ArrowLeft, Flag, Loader2 } from 'lucide-react'
import { useNavigate, useParams } from 'react-router-dom'
import { useFeature } from '../hooks'

export function FeatureDetailPage() {
  const navigate = useNavigate()
  const { featureId } = useParams<{ featureId: string }>()
  const { data: feature, isLoading } = useFeature(Number(featureId))

  return (
    <div className="space-y-6">
      <button
        onClick={() => navigate('/features')}
        className="inline-flex items-center gap-1 text-sm text-[var(--color-text-secondary)] hover:text-[var(--color-text-primary)] transition-colors"
      >
        <ArrowLeft className="size-4" />
        Back to Features
      </button>

      {isLoading ? (
        <div className="flex items-center justify-center py-16">
          <Loader2 className="size-8 animate-spin text-[var(--color-text-tertiary)]" />
        </div>
      ) : feature ? (
        <Card>
          <CardHeader>
            <div className="flex items-center gap-3">
              <div className={`flex size-10 items-center justify-center rounded-lg ${
                feature.enabled
                  ? 'bg-[var(--color-success-light)] text-[var(--color-success)]'
                  : 'bg-[var(--color-bg-tertiary)] text-[var(--color-text-tertiary)]'
              }`}>
                <Flag className="size-5" />
              </div>
              <div>
                <CardTitle>{feature.key}</CardTitle>
                <p className="text-sm text-[var(--color-text-secondary)]">{feature.description || 'No description'}</p>
              </div>
            </div>
          </CardHeader>
          <CardContent className="space-y-3">
            <div className="flex justify-between border-b border-[var(--color-border)] pb-2">
              <span className="text-sm text-[var(--color-text-secondary)]">ID</span>
              <span className="text-sm font-mono text-[var(--color-text-primary)]">{feature.id}</span>
            </div>
            <div className="flex justify-between border-b border-[var(--color-border)] pb-2">
              <span className="text-sm text-[var(--color-text-secondary)]">Status</span>
              <span className={`text-sm font-medium ${
                feature.enabled ? 'text-[var(--color-success)]' : 'text-[var(--color-text-tertiary)]'
              }`}>
                {feature.enabled ? 'Enabled' : 'Disabled'}
              </span>
            </div>
            <div className="flex justify-between border-b border-[var(--color-border)] pb-2">
              <span className="text-sm text-[var(--color-text-secondary)]">Domain ID</span>
              <span className="text-sm font-mono text-[var(--color-text-primary)]">{feature.domainId}</span>
            </div>
            <div className="flex justify-between border-b border-[var(--color-border)] pb-2">
              <span className="text-sm text-[var(--color-text-secondary)]">Created</span>
              <span className="text-sm text-[var(--color-text-primary)]">{new Date(feature.createdAt).toLocaleString()}</span>
            </div>
          </CardContent>
        </Card>
      ) : (
        <Card>
          <CardContent className="py-12 text-center text-sm text-[var(--color-text-tertiary)]">Feature not found</CardContent>
        </Card>
      )}
    </div>
  )
}
