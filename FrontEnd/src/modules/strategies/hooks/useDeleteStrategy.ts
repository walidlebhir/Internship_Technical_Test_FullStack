import { useMutation, useQueryClient } from '@tanstack/react-query'
import { strategyClient } from '../api'
import { queryKeys } from '@/shared/utils'

export function useDeleteStrategy() {
  const queryClient = useQueryClient()

  return useMutation<void, Error, number>({
    mutationFn: (id) => strategyClient.delete(id),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: queryKeys.strategies.all })
    },
  })
}
