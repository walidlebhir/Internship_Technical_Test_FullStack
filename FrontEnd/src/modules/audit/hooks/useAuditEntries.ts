import { useQuery } from '@tanstack/react-query'
import { auditClient } from '../api'
import { queryKeys } from '@/shared/utils'
import type { AuditFilters, AuditEntry } from '../types'
import type { PaginatedResponse } from '@/shared/types'

export function useAuditEntries(filters?: AuditFilters) {
  return useQuery<PaginatedResponse<AuditEntry>>({
    queryKey: queryKeys.audit.list(filters),
    queryFn: () => auditClient.getAll(filters),
  })
}
