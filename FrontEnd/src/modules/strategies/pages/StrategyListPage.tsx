import { useState } from 'react'
import { Button, Card, CardContent } from '@/shared/components'
import { GitBranch, Plus, Search, X, Loader2, Edit3, Trash2, ToggleLeft, ToggleRight } from 'lucide-react'
import { useStrategies, useCreateStrategy, useUpdateStrategy, useDeleteStrategy, useToggleStrategy } from '../hooks'
import { useFeatures } from '@/modules/features/hooks'
import type { Strategy, StrategyType } from '../types'

export function StrategyListPage() {
  const [showModal, setShowModal] = useState(false)
  const [editing, setEditing] = useState<Strategy | null>(null)
  const [deleting, setDeleting] = useState<Strategy | null>(null)
  const [type, setType] = useState<StrategyType>('PERCENTAGE')
  const [config, setConfig] = useState('')
  const [id_feature, setIdFeature] = useState(0)
  const { data: strategies, isLoading } = useStrategies()
  const { data: features } = useFeatures()
  const createStrategy = useCreateStrategy()
  const updateStrategy = useUpdateStrategy()
  const deleteStrategy = useDeleteStrategy()
  const toggleStrategy = useToggleStrategy()

  const openCreate = () => {
    setEditing(null)
    setType('PERCENTAGE')
    setConfig('')
    setIdFeature(0)
    setShowModal(true)
  }

  const openEdit = (s: Strategy) => {
    setEditing(s)
    setType(s.type)
    setConfig(s.config)
    setIdFeature(s.featureId)
    setShowModal(true)
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!config.trim() || !id_feature) return
    try {
      if (editing) {
        await updateStrategy.mutateAsync({ id: editing.id, type, config: config.trim() })
      } else {
        await createStrategy.mutateAsync({ type, config: config.trim(), id_feature })
      }
      setType('PERCENTAGE')
      setConfig('')
      setIdFeature(0)
      setEditing(null)
      setShowModal(false)
    } catch { /* handled */ }
  }

  const handleDelete = async () => {
    if (!deleting) return
    try {
      await deleteStrategy.mutateAsync(deleting.id)
      setDeleting(null)
    } catch { /* handled */ }
  }

  const configPlaceholder = {
    PERCENTAGE: '{"percentage": 50}',
    ALLOWLIST: '{"userIds": ["user1", "user2"]}',
    ENVIRONMENT: '{"environments": ["production", "staging"]}',
    DATE: '{"startDate": "2025-01-01", "endDate": "2025-12-31"}',
  }[type]

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-[var(--color-text-primary)]">Strategies</h1>
          <p className="mt-1 text-sm text-[var(--color-text-secondary)]">Configure rollout and targeting strategies</p>
        </div>
        <Button leftIcon={<Plus className="size-4" />} onClick={openCreate}>New Strategy</Button>
      </div>

      <Card>
        <CardContent className="p-6">
          <div className="relative mb-4">
            <Search className="absolute left-3 top-1/2 size-4 -translate-y-1/2 text-[var(--color-text-tertiary)]" />
            <input
              type="text"
              placeholder="Search strategies..."
              className="h-10 w-full rounded-lg border border-[var(--color-border)] bg-[var(--color-bg-primary)] pl-10 pr-4 text-sm text-[var(--color-text-primary)] placeholder:text-[var(--color-text-tertiary)] focus:outline-none focus:ring-2 focus:ring-[var(--color-accent)]"
            />
          </div>

          {isLoading ? (
            <div className="flex items-center justify-center py-16">
              <Loader2 className="size-8 animate-spin text-[var(--color-text-tertiary)]" />
            </div>
          ) : strategies && strategies.length > 0 ? (
            <div className="divide-y divide-[var(--color-border)]">
              {strategies.map((s) => (
                <div key={s.id} className="flex items-center justify-between py-3">
                  <div className="flex items-center gap-3 min-w-0">
                    <div className="flex size-9 shrink-0 items-center justify-center rounded-lg bg-[var(--color-accent-light)]">
                      <GitBranch className="size-4 text-[var(--color-accent)]" />
                    </div>
                    <div className="min-w-0">
                      <p className="text-sm font-medium text-[var(--color-text-primary)]">{s.type}</p>
                      <p className="text-xs text-[var(--color-text-tertiary)] truncate">Feature #{s.featureId}</p>
                    </div>
                  </div>
                  <div className="flex items-center gap-1 shrink-0 ml-3">
                    <button
                      onClick={() => toggleStrategy.mutate({ id: s.id, enable: !s.active })}
                      className={`rounded-lg p-1.5 transition-colors ${
                        s.active
                          ? 'text-[var(--color-success)] hover:bg-[var(--color-success-light)]'
                          : 'text-[var(--color-text-tertiary)] hover:bg-[var(--color-bg-tertiary)]'
                      }`}
                      title={s.active ? 'Disable' : 'Enable'}
                    >
                      {s.active ? <ToggleRight className="size-4" /> : <ToggleLeft className="size-4" />}
                    </button>
                    <button
                      onClick={() => openEdit(s)}
                      className="rounded-lg p-1.5 text-[var(--color-text-tertiary)] hover:bg-[var(--color-bg-tertiary)] hover:text-[var(--color-text-primary)] transition-colors"
                    >
                      <Edit3 className="size-4" />
                    </button>
                    <button
                      onClick={() => setDeleting(s)}
                      className="rounded-lg p-1.5 text-[var(--color-text-tertiary)] hover:bg-[var(--color-error-light)] hover:text-[var(--color-error)] transition-colors"
                    >
                      <Trash2 className="size-4" />
                    </button>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="flex flex-col items-center justify-center rounded-xl border-2 border-dashed border-[var(--color-border)] py-16">
              <GitBranch className="size-12 text-[var(--color-text-tertiary)]" />
              <p className="mt-4 text-sm font-medium text-[var(--color-text-secondary)]">No strategies found</p>
              <p className="mt-1 text-xs text-[var(--color-text-tertiary)]">Create your first strategy to get started</p>
              <Button className="mt-4" size="sm" leftIcon={<Plus className="size-4" />} onClick={openCreate}>Create Strategy</Button>
            </div>
          )}
        </CardContent>
      </Card>

      {/* Create / Edit Modal */}
      {showModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
          <div className="w-full max-w-md rounded-xl border border-[var(--color-border)] bg-[var(--color-bg-elevated)] shadow-xl">
            <div className="flex items-center justify-between border-b border-[var(--color-border)] px-6 py-4">
              <h3 className="text-lg font-semibold text-[var(--color-text-primary)]">
                {editing ? 'Edit Strategy' : 'New Strategy'}
              </h3>
              <button onClick={() => { setShowModal(false); setEditing(null) }} className="rounded-lg p-1 text-[var(--color-text-tertiary)] hover:bg-[var(--color-bg-tertiary)]">
                <X className="size-5" />
              </button>
            </div>
            <form onSubmit={handleSubmit} className="space-y-4 p-6">
              <div className="space-y-1.5">
                <label className="text-sm font-medium text-[var(--color-text-secondary)]">Type</label>
                <select
                  value={type}
                  onChange={(e) => setType(e.target.value as StrategyType)}
                  className="h-10 w-full rounded-lg border border-[var(--color-border)] bg-[var(--color-bg-primary)] px-3 text-sm text-[var(--color-text-primary)] focus:outline-none focus:ring-2 focus:ring-[var(--color-accent)]"
                >
                  <option value="PERCENTAGE">Percentage</option>
                  <option value="ALLOWLIST">Allow List</option>
                  <option value="ENVIRONMENT">Environment</option>
                  <option value="DATE">Date Range</option>
                </select>
              </div>
              {!editing && (
                <div className="space-y-1.5">
                  <label className="text-sm font-medium text-[var(--color-text-secondary)]">Feature</label>
                  <select
                    value={id_feature}
                    onChange={(e) => setIdFeature(Number(e.target.value))}
                    className="h-10 w-full rounded-lg border border-[var(--color-border)] bg-[var(--color-bg-primary)] px-3 text-sm text-[var(--color-text-primary)] focus:outline-none focus:ring-2 focus:ring-[var(--color-accent)]"
                  >
                    <option value={0}>Select a feature...</option>
                    {features?.map((f) => (
                      <option key={f.id} value={f.id}>{f.key}</option>
                    ))}
                  </select>
                </div>
              )}
              <div className="space-y-1.5">
                <label className="text-sm font-medium text-[var(--color-text-secondary)]">Config (JSON)</label>
                <textarea
                  value={config}
                  onChange={(e) => setConfig(e.target.value)}
                  placeholder={configPlaceholder}
                  rows={4}
                  className="h-28 w-full rounded-lg border border-[var(--color-border)] bg-[var(--color-bg-primary)] px-3 py-2 text-sm font-mono text-[var(--color-text-primary)] placeholder:text-[var(--color-text-tertiary)] focus:outline-none focus:ring-2 focus:ring-[var(--color-accent)] resize-none"
                />
              </div>
              <div className="flex justify-end gap-3 pt-2">
                <Button type="button" variant="ghost" onClick={() => { setShowModal(false); setEditing(null) }}>Cancel</Button>
                <Button type="submit" isLoading={createStrategy.isPending || updateStrategy.isPending} disabled={!config.trim() || (!editing && !id_feature)}>
                  {editing ? 'Update Strategy' : 'Create Strategy'}
                </Button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Delete Confirmation */}
      {deleting && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
          <div className="w-full max-w-sm rounded-xl border border-[var(--color-border)] bg-[var(--color-bg-elevated)] p-6 shadow-xl">
            <h3 className="text-lg font-semibold text-[var(--color-text-primary)]">Delete Strategy</h3>
            <p className="mt-2 text-sm text-[var(--color-text-secondary)]">
              Are you sure you want to delete this <strong className="text-[var(--color-text-primary)]">{deleting.type}</strong> strategy? This action cannot be undone.
            </p>
            <div className="flex justify-end gap-3 mt-6">
              <Button type="button" variant="ghost" onClick={() => setDeleting(null)}>Cancel</Button>
              <Button variant="danger" onClick={handleDelete} isLoading={deleteStrategy.isPending}>Delete</Button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
