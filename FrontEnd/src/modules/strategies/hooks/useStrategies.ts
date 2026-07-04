import { useQuery } from '@tanstack/react-query'
import { strategyClient } from '../api'
import { queryKeys } from '@/shared/utils'
import type { Strategy } from '../types'

export function useStrategies() {
  return useQuery<Strategy[]>({
    queryKey: queryKeys.strategies.list(),
    queryFn: () => strategyClient.getAll(),
  })
}
