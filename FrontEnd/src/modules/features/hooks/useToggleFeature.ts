import { useMutation, useQueryClient } from '@tanstack/react-query'
import { featureClient } from '../api'
import { queryKeys } from '@/shared/utils'
import type { Feature } from '../types'

interface ToggleInput {
  id: number
  enable: boolean
}

export function useToggleFeature() {
  const queryClient = useQueryClient()

  return useMutation<Feature, Error, ToggleInput>({
    mutationFn: ({ id, enable }) =>
      enable ? featureClient.enable(id) : featureClient.disable(id),
    onSuccess: (_data, variables) => {
      void queryClient.invalidateQueries({ queryKey: queryKeys.features.all })
      void queryClient.invalidateQueries({ queryKey: queryKeys.features.detail(String(variables.id)) })
    },
  })
}
