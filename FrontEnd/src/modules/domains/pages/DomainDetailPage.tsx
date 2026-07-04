import { Card, CardContent, CardHeader, CardTitle } from '@/shared/components'
import { ArrowLeft, Globe, Loader2 } from 'lucide-react'
import { useNavigate, useParams } from 'react-router-dom'
import { useDomain } from '../hooks'

export function DomainDetailPage() {
  const navigate = useNavigate()
  const { domainId } = useParams<{ domainId: string }>()
  const { data: domain, isLoading } = useDomain(domainId!)

  return (
    <div className="space-y-6">
      <button
        onClick={() => navigate('/domains')}
        className="inline-flex items-center gap-1 text-sm text-[var(--color-text-secondary)] hover:text-[var(--color-text-primary)] transition-colors"
      >
        <ArrowLeft className="size-4" />
        Back to Domains
      </button>

      {isLoading ? (
        <div className="flex items-center justify-center py-16">
          <Loader2 className="size-8 animate-spin text-[var(--color-text-tertiary)]" />
        </div>
      ) : domain ? (
        <Card>
          <CardHeader>
            <div className="flex items-center gap-3">
              <div className="flex size-10 items-center justify-center rounded-lg bg-[var(--color-accent-light)]">
                <Globe className="size-5 text-[var(--color-accent)]" />
              </div>
              <div>
                <CardTitle>{domain.name}</CardTitle>
                <p className="text-sm text-[var(--color-text-secondary)]">{domain.description || 'No description'}</p>
              </div>
            </div>
          </CardHeader>
          <CardContent className="space-y-3">
            <div className="flex justify-between border-b border-[var(--color-border)] pb-2">
              <span className="text-sm text-[var(--color-text-secondary)]">ID</span>
              <span className="text-sm font-mono text-[var(--color-text-primary)]">{domain.id}</span>
            </div>
            <div className="flex justify-between border-b border-[var(--color-border)] pb-2">
              <span className="text-sm text-[var(--color-text-secondary)]">Created</span>
              <span className="text-sm text-[var(--color-text-primary)]">{new Date(domain.createdAt).toLocaleString()}</span>
            </div>
          </CardContent>
        </Card>
      ) : (
        <Card>
          <CardContent className="py-12 text-center text-sm text-[var(--color-text-tertiary)]">Domain not found</CardContent>
        </Card>
      )}
    </div>
  )
}
