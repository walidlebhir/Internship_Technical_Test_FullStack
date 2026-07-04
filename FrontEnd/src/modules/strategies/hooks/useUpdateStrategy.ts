import { useMutation, useQueryClient } from '@tanstack/react-query'
import { strategyClient } from '../api'
import { queryKeys } from '@/shared/utils'
import type { Strategy, StrategyType } from '../types'

interface UpdateInput {
  id: number
  type: StrategyType
  config: string
}

export function useUpdateStrategy() {
  const queryClient = useQueryClient()

  return useMutation<Strategy, Error, UpdateInput>({
    mutationFn: ({ id, type, config }) =>
      strategyClient.update(id, { type, config }),
    onSuccess: (_data, variables) => {
      void queryClient.invalidateQueries({ queryKey: queryKeys.strategies.all })
      void queryClient.invalidateQueries({ queryKey: queryKeys.strategies.detail(String(variables.id)) })
    },
  })
}
