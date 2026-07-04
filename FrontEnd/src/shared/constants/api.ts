export const API_ENDPOINTS = {
  DOMAINS: {
    BASE: '/api/v1/domains',
    BY_ID: (id: string) => `/api/v1/domains/${id}`,
  },
  FEATURES: {
    BASE: '/api/v1/features',
    BY_ID: (id: number) => `/api/v1/features/${id}`,
    ENABLE: (id: number) => `/api/v1/features/${id}/enable`,
    DISABLE: (id: number) => `/api/v1/features/${id}/disable`,
  },
  STRATEGIES: {
    BASE: '/api/v1/strategies',
    BY_ID: (id: number) => `/api/v1/strategies/${id}`,
    BY_FEATURE_ID: (featureId: number) => `/api/v1/features/${featureId}/strategies`,
    ENABLE: (id: number) => `/api/v1/strategies/${id}/enable`,
    DISABLE: (id: number) => `/api/v1/strategies/${id}/disable`,
  },
  EVALUATION: {
    EVALUATE: (featureKey: string) => `/api/v1/features/${featureKey}/evaluate`,
  },
  AUDIT: {
    BASE: '/api/v1/audit',
  },
} as const

export const API_HEADERS = {
  AUTHORIZATION: 'Authorization',
  BEARER: 'Bearer',
  ACCEPT_LANGUAGE: 'Accept-Language',
} as const
