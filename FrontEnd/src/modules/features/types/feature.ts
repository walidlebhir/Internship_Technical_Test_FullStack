export interface Feature {
  id: number
  key: string
  description: string
  enabled: boolean
  domainId: string
  createdAt: string
  updatedAt: string
}

export interface CreateFeatureRequest {
  key: string
  description?: string
  id_domain: string
}

export interface UpdateFeatureRequest {
  key?: string
  description?: string
  id_domain?: string
}

export interface FeatureFilters extends Record<string, unknown> {
  search?: string
  page?: number
  size?: number
}
