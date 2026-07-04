import { useMutation, useQueryClient } from '@tanstack/react-query'
import { featureClient } from '../api'
import { queryKeys } from '@/shared/utils'
import type { CreateFeatureRequest, Feature } from '../types'

export function useCreateFeature() {
  const queryClient = useQueryClient()

  return useMutation<Feature, Error, CreateFeatureRequest>({
    mutationFn: (data) => featureClient.create(data),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: queryKeys.features.all })
    },
  })
}
