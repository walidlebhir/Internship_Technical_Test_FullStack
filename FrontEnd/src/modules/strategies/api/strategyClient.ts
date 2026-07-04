import { apiClient } from '@/shared/services'
import { API_ENDPOINTS } from '@/shared/constants'
import type { Strategy, CreateStrategyRequest, UpdateStrategyRequest } from '../types'

export const strategyClient = {
  getAll(): Promise<Strategy[]> {
    return apiClient.get(API_ENDPOINTS.STRATEGIES.BASE)
  },

  getById(id: number): Promise<Strategy> {
    return apiClient.get(API_ENDPOINTS.STRATEGIES.BY_ID(id))
  },

  getByFeatureId(featureId: number): Promise<Strategy[]> {
    return apiClient.get(API_ENDPOINTS.STRATEGIES.BY_FEATURE_ID(featureId))
  },

  create(data: CreateStrategyRequest): Promise<Strategy> {
    return apiClient.post(API_ENDPOINTS.STRATEGIES.BASE, data)
  },

  update(id: number, data: UpdateStrategyRequest): Promise<Strategy> {
    return apiClient.put(API_ENDPOINTS.STRATEGIES.BY_ID(id), data)
  },

  delete(id: number): Promise<void> {
    return apiClient.delete(API_ENDPOINTS.STRATEGIES.BY_ID(id))
  },

  enable(id: number): Promise<Strategy> {
    return apiClient.patch(API_ENDPOINTS.STRATEGIES.ENABLE(id))
  },

  disable(id: number): Promise<Strategy> {
    return apiClient.patch(API_ENDPOINTS.STRATEGIES.DISABLE(id))
  },
}
