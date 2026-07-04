import { apiClient } from '@/shared/services'
import { API_ENDPOINTS } from '@/shared/constants'
import type { Domain, CreateDomainRequest, UpdateDomainRequest } from '../types'

export const domainClient = {
  getAll(): Promise<Domain[]> {
    return apiClient.get(API_ENDPOINTS.DOMAINS.BASE)
  },

  getById(id: string): Promise<Domain> {
    return apiClient.get(API_ENDPOINTS.DOMAINS.BY_ID(id))
  },

  create(data: CreateDomainRequest): Promise<Domain> {
    return apiClient.post(API_ENDPOINTS.DOMAINS.BASE, data)
  },

  update(id: string, data: UpdateDomainRequest): Promise<Domain> {
    return apiClient.put(API_ENDPOINTS.DOMAINS.BY_ID(id), data)
  },

  delete(id: string): Promise<void> {
    return apiClient.delete(API_ENDPOINTS.DOMAINS.BY_ID(id))
  },
}
