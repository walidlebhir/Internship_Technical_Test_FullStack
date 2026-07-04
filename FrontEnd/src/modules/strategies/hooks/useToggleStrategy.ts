import { useMutation, useQueryClient } from '@tanstack/react-query'
import { strategyClient } from '../api'
import { queryKeys } from '@/shared/utils'
import type { Strategy } from '../types'

interface ToggleInput {
  id: number
  enable: boolean
}

export function useToggleStrategy() {
  const queryClient = useQueryClient()

  return useMutation<Strategy, Error, ToggleInput>({
    mutationFn: ({ id, enable }) =>
      enable ? strategyClient.enable(id) : strategyClient.disable(id),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: queryKeys.strategies.all })
    },
  })
}
