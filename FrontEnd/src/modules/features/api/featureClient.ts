import { apiClient } from '@/shared/services'
import { API_ENDPOINTS } from '@/shared/constants'
import type { Feature, CreateFeatureRequest, UpdateFeatureRequest } from '../types'

export const featureClient = {
  getAll(): Promise<Feature[]> {
    return apiClient.get(API_ENDPOINTS.FEATURES.BASE)
  },

  getById(id: number): Promise<Feature> {
    return apiClient.get(API_ENDPOINTS.FEATURES.BY_ID(id))
  },

  create(data: CreateFeatureRequest): Promise<Feature> {
    return apiClient.post(API_ENDPOINTS.FEATURES.BASE, data)
  },

  update(id: number, data: UpdateFeatureRequest): Promise<Feature> {
    return apiClient.put(API_ENDPOINTS.FEATURES.BY_ID(id), data)
  },

  delete(id: number): Promise<void> {
    return apiClient.delete(API_ENDPOINTS.FEATURES.BY_ID(id))
  },

  enable(id: number): Promise<Feature> {
    return apiClient.patch(API_ENDPOINTS.FEATURES.ENABLE(id))
  },

  disable(id: number): Promise<Feature> {
    return apiClient.patch(API_ENDPOINTS.FEATURES.DISABLE(id))
  },
}
