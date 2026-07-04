import { useQuery } from '@tanstack/react-query'
import { strategyClient } from '../api'
import { queryKeys } from '@/shared/utils'
import type { Strategy } from '../types'

export function useStrategy(id: number) {
  return useQuery<Strategy>({
    queryKey: queryKeys.strategies.detail(String(id)),
    queryFn: () => strategyClient.getById(id),
    enabled: !!id,
  })
}
