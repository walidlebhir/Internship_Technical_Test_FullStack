import { useMutation, useQueryClient } from '@tanstack/react-query'
import { domainClient } from '../api'
import { queryKeys } from '@/shared/utils'
import type { CreateDomainRequest, Domain } from '../types'

export function useCreateDomain() {
  const queryClient = useQueryClient()

  return useMutation<Domain, Error, CreateDomainRequest>({
    mutationFn: (data) => domainClient.create(data),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: queryKeys.domains.all })
    },
  })
}
