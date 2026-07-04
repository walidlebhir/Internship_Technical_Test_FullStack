export const queryKeys = {
  domains: {
    all: ['domains'] as const,
    list: (params?: Record<string, unknown>) => ['domains', 'list', params] as const,
    detail: (id: string) => ['domains', id] as const,
  },
  features: {
    all: ['features'] as const,
    list: (params?: Record<string, unknown>) => ['features', 'list', params] as const,
    detail: (id: string) => ['features', id] as const,
  },
  strategies: {
    all: ['strategies'] as const,
    list: (params?: Record<string, unknown>) => ['strategies', 'list', params] as const,
    detail: (id: string) => ['strategies', id] as const,
  },
  evaluation: {
    result: (params?: Record<string, unknown>) => ['evaluation', params] as const,
  },
  audit: {
    all: ['audit'] as const,
    list: (params?: Record<string, unknown>) => ['audit', 'list', params] as const,
  },
} as const
