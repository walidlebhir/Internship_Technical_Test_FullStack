import type { PaginatedResponse } from '@/shared/types'

export interface AuditEntry {
  id: number
  timestamp: string
  action: string
  entityType: string
  entityId: string
  who: string
}

export interface AuditFilters {
  entityType?: string
  page?: number
  size?: number
}

export type AuditResponse = PaginatedResponse<AuditEntry>
