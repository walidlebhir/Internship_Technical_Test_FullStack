export type StrategyType = 'PERCENTAGE' | 'ALLOWLIST' | 'ENVIRONMENT' | 'DATE'

export interface Strategy {
  id: number
  type: StrategyType
  config: string
  active: boolean
  featureId: number
}

export interface CreateStrategyRequest {
  type: StrategyType
  config: string
  id_feature: number
}

export interface UpdateStrategyRequest {
  type?: StrategyType
  config?: string
}

export interface StrategyFilters extends Record<string, unknown> {
  featureId?: number
  page?: number
  size?: number
}
