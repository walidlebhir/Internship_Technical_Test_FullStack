import { useMutation, useQueryClient } from '@tanstack/react-query'
import { strategyClient } from '../api'
import { queryKeys } from '@/shared/utils'
import type { CreateStrategyRequest, Strategy } from '../types'

export function useCreateStrategy() {
  const queryClient = useQueryClient()

  return useMutation<Strategy, Error, CreateStrategyRequest>({
    mutationFn: (data) => strategyClient.create(data),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: queryKeys.strategies.all })
    },
  })
}
