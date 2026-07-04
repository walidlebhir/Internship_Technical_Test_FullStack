import { apiClient } from '@/shared/services'
import { API_ENDPOINTS } from '@/shared/constants'
import type { AuditEntry, AuditFilters } from '../types'
import type { PaginatedResponse } from '@/shared/types'

export const auditClient = {
  getAll(filters?: AuditFilters): Promise<PaginatedResponse<AuditEntry>> {
    return apiClient.get(API_ENDPOINTS.AUDIT.BASE, { params: filters })
  },
}
