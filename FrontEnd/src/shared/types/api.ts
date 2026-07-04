export interface ApiError {
  status: number
  message: string
  errors?: Record<string, string[]>
  path?: string
  timestamp: string
}

export interface PaginatedResponse<TItem> {
  content: TItem[]
  number: number
  size: number
  totalElements: number
  totalPages: number
  first: boolean
  last: boolean
}

export interface PaginationParams {
  page?: number
  size?: number
  sort?: string[]
}
