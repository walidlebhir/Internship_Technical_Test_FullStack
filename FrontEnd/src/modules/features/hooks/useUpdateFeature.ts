import { useMutation, useQueryClient } from '@tanstack/react-query'
import { featureClient } from '../api'
import { queryKeys } from '@/shared/utils'
import type { Feature } from '../types'

interface UpdateInput {
  id: number
  key?: string
  description?: string
  id_domain?: string
}

export function useUpdateFeature() {
  const queryClient = useQueryClient()

  return useMutation<Feature, Error, UpdateInput>({
    mutationFn: ({ id, key, description, id_domain }) =>
      featureClient.update(id, { key, description, id_domain }),
    onSuccess: (_data, variables) => {
      void queryClient.invalidateQueries({ queryKey: queryKeys.features.all })
      void queryClient.invalidateQueries({ queryKey: queryKeys.features.detail(String(variables.id)) })
    },
  })
}
