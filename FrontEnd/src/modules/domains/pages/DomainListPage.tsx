import { useState } from 'react'
import { Button, Card, CardContent } from '@/shared/components'
import { Globe, Plus, Search, X, Loader2, Edit3, Trash2 } from 'lucide-react'
import { useDomains, useCreateDomain, useUpdateDomain, useDeleteDomain } from '../hooks'
import type { Domain } from '../types'

export function DomainListPage() {
  const [showModal, setShowModal] = useState(false)
  const [editing, setEditing] = useState<Domain | null>(null)
  const [deleting, setDeleting] = useState<Domain | null>(null)
  const [name, setName] = useState('')
  const [description, setDescription] = useState('')
  const { data: domains, isLoading } = useDomains()
  const createDomain = useCreateDomain()
  const updateDomain = useUpdateDomain()
  const deleteDomain = useDeleteDomain()

  const openCreate = () => {
    setEditing(null)
    setName('')
    setDescription('')
    setShowModal(true)
  }

  const openEdit = (d: Domain) => {
    setEditing(d)
    setName(d.name)
    setDescription(d.description ?? '')
    setShowModal(true)
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!name.trim()) return
    try {
      if (editing) {
        await updateDomain.mutateAsync({ id: editing.id, name: name.trim(), description: description.trim() || undefined })
      } else {
        await createDomain.mutateAsync({ name: name.trim(), description: description.trim() || undefined })
      }
      setName('')
      setDescription('')
      setEditing(null)
      setShowModal(false)
    } catch { /* handled by react-query */ }
  }

  const handleDelete = async () => {
    if (!deleting) return
    try {
      await deleteDomain.mutateAsync(deleting.id)
      setDeleting(null)
    } catch { /* handled */ }
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-[var(--color-text-primary)]">Domains</h1>
          <p className="mt-1 text-sm text-[var(--color-text-secondary)]">Manage your domains and environments</p>
        </div>
        <Button leftIcon={<Plus className="size-4" />} onClick={openCreate}>New Domain</Button>
      </div>

      <Card>
        <CardContent className="p-6">
          <div className="relative mb-4">
            <Search className="absolute left-3 top-1/2 size-4 -translate-y-1/2 text-[var(--color-text-tertiary)]" />
            <input
              type="text"
              placeholder="Search domains..."
              className="h-10 w-full rounded-lg border border-[var(--color-border)] bg-[var(--color-bg-primary)] pl-10 pr-4 text-sm text-[var(--color-text-primary)] placeholder:text-[var(--color-text-tertiary)] focus:outline-none focus:ring-2 focus:ring-[var(--color-accent)]"
            />
          </div>

          {isLoading ? (
            <div className="flex items-center justify-center py-16">
              <Loader2 className="size-8 animate-spin text-[var(--color-text-tertiary)]" />
            </div>
          ) : domains && domains.length > 0 ? (
            <div className="divide-y divide-[var(--color-border)]">
              {domains.map((domain) => (
                <div key={domain.id} className="flex items-center justify-between py-3">
                  <div className="flex items-center gap-3 min-w-0">
                    <div className="flex size-9 shrink-0 items-center justify-center rounded-lg bg-[var(--color-accent-light)]">
                      <Globe className="size-4 text-[var(--color-accent)]" />
                    </div>
                    <div className="min-w-0">
                      <p className="text-sm font-medium text-[var(--color-text-primary)] truncate">{domain.name}</p>
                      {domain.description && (
                        <p className="text-xs text-[var(--color-text-tertiary)] truncate">{domain.description}</p>
                      )}
                    </div>
                  </div>
                  <div className="flex items-center gap-1 shrink-0 ml-3">
                    <button
                      onClick={() => openEdit(domain)}
                      className="rounded-lg p-1.5 text-[var(--color-text-tertiary)] hover:bg-[var(--color-bg-tertiary)] hover:text-[var(--color-text-primary)] transition-colors"
                    >
                      <Edit3 className="size-4" />
                    </button>
                    <button
                      onClick={() => setDeleting(domain)}
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
              <Globe className="size-12 text-[var(--color-text-tertiary)]" />
              <p className="mt-4 text-sm font-medium text-[var(--color-text-secondary)]">No domains found</p>
              <p className="mt-1 text-xs text-[var(--color-text-tertiary)]">Create your first domain to get started</p>
              <Button className="mt-4" size="sm" leftIcon={<Plus className="size-4" />} onClick={openCreate}>Create Domain</Button>
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
                {editing ? 'Edit Domain' : 'New Domain'}
              </h3>
              <button onClick={() => { setShowModal(false); setEditing(null) }} className="rounded-lg p-1 text-[var(--color-text-tertiary)] hover:bg-[var(--color-bg-tertiary)]">
                <X className="size-5" />
              </button>
            </div>
            <form onSubmit={handleSubmit} className="space-y-4 p-6">
              <div className="space-y-1.5">
                <label className="text-sm font-medium text-[var(--color-text-secondary)]">Name</label>
                <input
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  placeholder="e.g. production"
                  className="h-10 w-full rounded-lg border border-[var(--color-border)] bg-[var(--color-bg-primary)] px-3 text-sm text-[var(--color-text-primary)] placeholder:text-[var(--color-text-tertiary)] focus:outline-none focus:ring-2 focus:ring-[var(--color-accent)]"
                  autoFocus
                />
              </div>
              <div className="space-y-1.5">
                <label className="text-sm font-medium text-[var(--color-text-secondary)]">Description (optional)</label>
                <textarea
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                  placeholder="Brief description of this domain"
                  rows={3}
                  className="h-24 w-full rounded-lg border border-[var(--color-border)] bg-[var(--color-bg-primary)] px-3 py-2 text-sm text-[var(--color-text-primary)] placeholder:text-[var(--color-text-tertiary)] focus:outline-none focus:ring-2 focus:ring-[var(--color-accent)] resize-none"
                />
              </div>
              <div className="flex justify-end gap-3 pt-2">
                <Button type="button" variant="ghost" onClick={() => { setShowModal(false); setEditing(null) }}>Cancel</Button>
                <Button type="submit" isLoading={createDomain.isPending || updateDomain.isPending} disabled={!name.trim()}>
                  {editing ? 'Update Domain' : 'Create Domain'}
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
            <h3 className="text-lg font-semibold text-[var(--color-text-primary)]">Delete Domain</h3>
            <p className="mt-2 text-sm text-[var(--color-text-secondary)]">
              Are you sure you want to delete <strong className="text-[var(--color-text-primary)]">{deleting.name}</strong>? This action cannot be undone.
            </p>
            <div className="flex justify-end gap-3 mt-6">
              <Button type="button" variant="ghost" onClick={() => setDeleting(null)}>Cancel</Button>
              <Button variant="danger" onClick={handleDelete} isLoading={deleteDomain.isPending}>Delete</Button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
