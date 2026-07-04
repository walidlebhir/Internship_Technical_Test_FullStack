import { useState } from 'react'
import { Button, Card, CardContent } from '@/shared/components'
import { Flag, Plus, Search, X, Loader2, Edit3, Trash2, ToggleLeft, ToggleRight } from 'lucide-react'
import { useFeatures, useCreateFeature, useUpdateFeature, useDeleteFeature, useToggleFeature } from '../hooks'
import { useDomains } from '@/modules/domains/hooks'
import type { Feature } from '../types'

export function FeatureListPage() {
  const [showModal, setShowModal] = useState(false)
  const [editing, setEditing] = useState<Feature | null>(null)
  const [deleting, setDeleting] = useState<Feature | null>(null)
  const [key, setKey] = useState('')
  const [description, setDescription] = useState('')
  const [id_domain, setIdDomain] = useState('')
  const { data: features, isLoading } = useFeatures()
  const { data: domains } = useDomains()
  const createFeature = useCreateFeature()
  const updateFeature = useUpdateFeature()
  const deleteFeature = useDeleteFeature()
  const toggleFeature = useToggleFeature()

  const openCreate = () => {
    setEditing(null)
    setKey('')
    setDescription('')
    setIdDomain('')
    setShowModal(true)
  }

  const openEdit = (f: Feature) => {
    setEditing(f)
    setKey(f.key)
    setDescription(f.description ?? '')
    setIdDomain(f.domainId)
    setShowModal(true)
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!key.trim() || !id_domain) return
    try {
      if (editing) {
        await updateFeature.mutateAsync({ id: editing.id, key: key.trim(), description: description.trim() || undefined })
      } else {
        await createFeature.mutateAsync({ key: key.trim(), description: description.trim() || undefined, id_domain })
      }
      setKey('')
      setDescription('')
      setIdDomain('')
      setEditing(null)
      setShowModal(false)
    } catch { /* handled */ }
  }

  const handleDelete = async () => {
    if (!deleting) return
    try {
      await deleteFeature.mutateAsync(deleting.id)
      setDeleting(null)
    } catch { /* handled */ }
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-[var(--color-text-primary)]">Features</h1>
          <p className="mt-1 text-sm text-[var(--color-text-secondary)]">Manage your feature flags across domains</p>
        </div>
        <Button leftIcon={<Plus className="size-4" />} onClick={openCreate}>New Feature</Button>
      </div>

      <Card>
        <CardContent className="p-6">
          <div className="relative mb-4">
            <Search className="absolute left-3 top-1/2 size-4 -translate-y-1/2 text-[var(--color-text-tertiary)]" />
            <input
              type="text"
              placeholder="Search features..."
              className="h-10 w-full rounded-lg border border-[var(--color-border)] bg-[var(--color-bg-primary)] pl-10 pr-4 text-sm text-[var(--color-text-primary)] placeholder:text-[var(--color-text-tertiary)] focus:outline-none focus:ring-2 focus:ring-[var(--color-accent)]"
            />
          </div>

          {isLoading ? (
            <div className="flex items-center justify-center py-16">
              <Loader2 className="size-8 animate-spin text-[var(--color-text-tertiary)]" />
            </div>
          ) : features && features.length > 0 ? (
            <div className="divide-y divide-[var(--color-border)]">
              {features.map((feature) => (
                <div key={feature.id} className="flex items-center justify-between py-3">
                  <div className="flex items-center gap-3 min-w-0">
                    <div className={`
                      flex size-9 shrink-0 items-center justify-center rounded-lg
                      ${feature.enabled
                        ? 'bg-[var(--color-success-light)] text-[var(--color-success)]'
                        : 'bg-[var(--color-bg-tertiary)] text-[var(--color-text-tertiary)]'}
                    `}>
                      <Flag className="size-4" />
                    </div>
                    <div className="min-w-0">
                      <p className="text-sm font-medium text-[var(--color-text-primary)] truncate">{feature.key}</p>
                      {feature.description && (
                        <p className="text-xs text-[var(--color-text-tertiary)] truncate">{feature.description}</p>
                      )}
                    </div>
                  </div>
                  <div className="flex items-center gap-1 shrink-0 ml-3">
                    <button
                      onClick={() => toggleFeature.mutate({ id: feature.id, enable: !feature.enabled })}
                      className={`rounded-lg p-1.5 transition-colors ${
                        feature.enabled
                          ? 'text-[var(--color-success)] hover:bg-[var(--color-success-light)]'
                          : 'text-[var(--color-text-tertiary)] hover:bg-[var(--color-bg-tertiary)]'
                      }`}
                      title={feature.enabled ? 'Disable' : 'Enable'}
                    >
                      {feature.enabled ? <ToggleRight className="size-4" /> : <ToggleLeft className="size-4" />}
                    </button>
                    <button
                      onClick={() => openEdit(feature)}
                      className="rounded-lg p-1.5 text-[var(--color-text-tertiary)] hover:bg-[var(--color-bg-tertiary)] hover:text-[var(--color-text-primary)] transition-colors"
                    >
                      <Edit3 className="size-4" />
                    </button>
                    <button
                      onClick={() => setDeleting(feature)}
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
              <Flag className="size-12 text-[var(--color-text-tertiary)]" />
              <p className="mt-4 text-sm font-medium text-[var(--color-text-secondary)]">No features found</p>
              <p className="mt-1 text-xs text-[var(--color-text-tertiary)]">Create your first feature flag to get started</p>
              <Button className="mt-4" size="sm" leftIcon={<Plus className="size-4" />} onClick={openCreate}>Create Feature</Button>
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
                {editing ? 'Edit Feature' : 'New Feature Flag'}
              </h3>
              <button onClick={() => { setShowModal(false); setEditing(null) }} className="rounded-lg p-1 text-[var(--color-text-tertiary)] hover:bg-[var(--color-bg-tertiary)]">
                <X className="size-5" />
              </button>
            </div>
            <form onSubmit={handleSubmit} className="space-y-4 p-6">
              <div className="space-y-1.5">
                <label className="text-sm font-medium text-[var(--color-text-secondary)]">Feature Key</label>
                <input
                  value={key}
                  onChange={(e) => setKey(e.target.value)}
                  placeholder="e.g. new-checkout"
                  className="h-10 w-full rounded-lg border border-[var(--color-border)] bg-[var(--color-bg-primary)] px-3 text-sm text-[var(--color-text-primary)] placeholder:text-[var(--color-text-tertiary)] focus:outline-none focus:ring-2 focus:ring-[var(--color-accent)]"
                  autoFocus
                />
              </div>
              {!editing && (
                <div className="space-y-1.5">
                  <label className="text-sm font-medium text-[var(--color-text-secondary)]">Domain</label>
                  <select
                    value={id_domain}
                    onChange={(e) => setIdDomain(e.target.value)}
                    className="h-10 w-full rounded-lg border border-[var(--color-border)] bg-[var(--color-bg-primary)] px-3 text-sm text-[var(--color-text-primary)] focus:outline-none focus:ring-2 focus:ring-[var(--color-accent)]"
                  >
                    <option value="">Select a domain...</option>
                    {domains?.map((d) => (
                      <option key={d.id} value={d.id}>{d.name}</option>
                    ))}
                  </select>
                </div>
              )}
              <div className="space-y-1.5">
                <label className="text-sm font-medium text-[var(--color-text-secondary)]">Description (optional)</label>
                <textarea
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                  placeholder="Brief description of this feature"
                  rows={3}
                  className="h-24 w-full rounded-lg border border-[var(--color-border)] bg-[var(--color-bg-primary)] px-3 py-2 text-sm text-[var(--color-text-primary)] placeholder:text-[var(--color-text-tertiary)] focus:outline-none focus:ring-2 focus:ring-[var(--color-accent)] resize-none"
                />
              </div>
              <div className="flex justify-end gap-3 pt-2">
                <Button type="button" variant="ghost" onClick={() => { setShowModal(false); setEditing(null) }}>Cancel</Button>
                <Button type="submit" isLoading={createFeature.isPending || updateFeature.isPending} disabled={!key.trim() || (!editing && !id_domain)}>
                  {editing ? 'Update Feature' : 'Create Feature'}
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
            <h3 className="text-lg font-semibold text-[var(--color-text-primary)]">Delete Feature</h3>
            <p className="mt-2 text-sm text-[var(--color-text-secondary)]">
              Are you sure you want to delete <strong className="text-[var(--color-text-primary)]">{deleting.key}</strong>? This action cannot be undone.
            </p>
            <div className="flex justify-end gap-3 mt-6">
              <Button type="button" variant="ghost" onClick={() => setDeleting(null)}>Cancel</Button>
              <Button variant="danger" onClick={handleDelete} isLoading={deleteFeature.isPending}>Delete</Button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
