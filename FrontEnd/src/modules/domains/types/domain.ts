export interface Domain {
  id: string
  name: string
  description: string
  createdAt: string
}

export interface CreateDomainRequest {
  name: string
  description?: string
}

export interface UpdateDomainRequest {
  name: string
  description?: string
}

export interface DomainFilters extends Record<string, unknown> {
  search?: string
  page?: number
  size?: number
}
