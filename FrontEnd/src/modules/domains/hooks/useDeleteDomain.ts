import { useMutation, useQueryClient } from '@tanstack/react-query'
import { domainClient } from '../api'
import { queryKeys } from '@/shared/utils'

export function useDeleteDomain() {
  const queryClient = useQueryClient()

  return useMutation<void, Error, string>({
    mutationFn: (id) => domainClient.delete(id),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: queryKeys.domains.all })
    },
  })
}
