import { useMutation, useQueryClient } from '@tanstack/react-query'
import { domainClient } from '../api'
import { queryKeys } from '@/shared/utils'
import type { Domain } from '../types'

interface UpdateInput {
  id: string
  name: string
  description?: string
}

export function useUpdateDomain() {
  const queryClient = useQueryClient()

  return useMutation<Domain, Error, UpdateInput>({
    mutationFn: ({ id, name, description }) =>
      domainClient.update(id, { name, description }),
    onSuccess: (_data, variables) => {
      void queryClient.invalidateQueries({ queryKey: queryKeys.domains.all })
      void queryClient.invalidateQueries({ queryKey: queryKeys.domains.detail(variables.id) })
    },
  })
}
