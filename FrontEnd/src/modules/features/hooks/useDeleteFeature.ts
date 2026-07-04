import { useMutation, useQueryClient } from '@tanstack/react-query'
import { featureClient } from '../api'
import { queryKeys } from '@/shared/utils'

export function useDeleteFeature() {
  const queryClient = useQueryClient()

  return useMutation<void, Error, number>({
    mutationFn: (id) => featureClient.delete(id),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: queryKeys.features.all })
    },
  })
}
