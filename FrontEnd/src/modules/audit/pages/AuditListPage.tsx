import { useState } from 'react'
import { Button, Card, CardContent } from '@/shared/components'
import { ClipboardList, Search, Loader2, RefreshCw, ChevronLeft, ChevronRight } from 'lucide-react'
import { useAuditEntries } from '../hooks'

export function AuditListPage() {
  const [entityFilter, setEntityFilter] = useState('')
  const [page, setPage] = useState(0)
  const { data, isLoading, isFetching } = useAuditEntries({
    entityType: entityFilter || undefined,
    page,
    size: 10,
  })

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-[var(--color-text-primary)]">Audit Log</h1>
          <p className="mt-1 text-sm text-[var(--color-text-secondary)]">Track all changes and events across the platform</p>
        </div>
        <Button variant="ghost" leftIcon={<RefreshCw className="size-4" />} onClick={() => setPage(0)}>
          Refresh
        </Button>
      </div>

      <Card>
        <CardContent className="p-6">
          <div className="flex gap-3 mb-4">
            <div className="relative flex-1">
              <Search className="absolute left-3 top-1/2 size-4 -translate-y-1/2 text-[var(--color-text-tertiary)]" />
              <input
                type="text"
                placeholder="Search audit entries..."
                className="h-10 w-full rounded-lg border border-[var(--color-border)] bg-[var(--color-bg-primary)] pl-10 pr-4 text-sm text-[var(--color-text-primary)] placeholder:text-[var(--color-text-tertiary)] focus:outline-none focus:ring-2 focus:ring-[var(--color-accent)]"
              />
            </div>
            <select
              value={entityFilter}
              onChange={(e) => { setEntityFilter(e.target.value); setPage(0) }}
              className="h-10 rounded-lg border border-[var(--color-border)] bg-[var(--color-bg-primary)] px-3 text-sm text-[var(--color-text-primary)] focus:outline-none focus:ring-2 focus:ring-[var(--color-accent)]"
            >
              <option value="">All Types</option>
              <option value="DOMAIN">Domain</option>
              <option value="FEATURE">Feature</option>
              <option value="STRATEGY">Strategy</option>
            </select>
          </div>

          {isLoading ? (
            <div className="flex items-center justify-center py-16">
              <Loader2 className="size-8 animate-spin text-[var(--color-text-tertiary)]" />
            </div>
          ) : data && data.content.length > 0 ? (
            <>
              <div className="divide-y divide-[var(--color-border)]">
                {data.content.map((entry) => (
                  <div key={entry.id} className="flex items-center gap-4 py-3">
                    <div className={`
                      flex size-8 shrink-0 items-center justify-center rounded-lg text-xs font-medium
                      ${entry.action === 'CREATE' ? 'bg-[var(--color-success-light)] text-[var(--color-success)]' : ''}
                      ${entry.action === 'UPDATE' ? 'bg-[var(--color-info-light)] text-[var(--color-info)]' : ''}
                      ${entry.action === 'DELETE' ? 'bg-[var(--color-error-light)] text-[var(--color-error)]' : ''}
                    `}>
                      {entry.action === 'CREATE' ? 'C' : entry.action === 'UPDATE' ? 'U' : 'D'}
                    </div>
                    <div className="flex-1 min-w-0">
                      <p className="text-sm font-medium text-[var(--color-text-primary)] truncate">
                        {entry.action} {entry.entityType} #{entry.entityId}
                      </p>
                      <p className="text-xs text-[var(--color-text-tertiary)]">by {entry.who}</p>
                    </div>
                    <span className="text-xs text-[var(--color-text-tertiary)] shrink-0">
                      {new Date(entry.timestamp).toLocaleString()}
                    </span>
                  </div>
                ))}
              </div>

              <div className="flex items-center justify-between pt-4 border-t border-[var(--color-border)] mt-4">
                <p className="text-sm text-[var(--color-text-secondary)]">
                  Page {data.number + 1} of {data.totalPages} ({data.totalElements} total)
                </p>
                <div className="flex gap-2">
                  <button
                    onClick={() => setPage((p) => Math.max(0, p - 1))}
                    disabled={data.first}
                    className="rounded-lg p-2 text-[var(--color-text-secondary)] hover:bg-[var(--color-bg-tertiary)] disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
                  >
                    <ChevronLeft className="size-4" />
                  </button>
                  <button
                    onClick={() => setPage((p) => p + 1)}
                    disabled={data.last}
                    className="rounded-lg p-2 text-[var(--color-text-secondary)] hover:bg-[var(--color-bg-tertiary)] disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
                  >
                    <ChevronRight className="size-4" />
                  </button>
                </div>
              </div>
            </>
          ) : (
            <div className="flex flex-col items-center justify-center rounded-xl border-2 border-dashed border-[var(--color-border)] py-16">
              <ClipboardList className="size-12 text-[var(--color-text-tertiary)]" />
              <p className="mt-4 text-sm font-medium text-[var(--color-text-secondary)]">No audit entries yet</p>
              <p className="mt-1 text-xs text-[var(--color-text-tertiary)]">Changes made to features and domains will appear here</p>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  )
}
