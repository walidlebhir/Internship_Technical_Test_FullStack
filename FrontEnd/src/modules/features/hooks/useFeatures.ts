import { useQuery } from '@tanstack/react-query'
import { featureClient } from '../api'
import { queryKeys } from '@/shared/utils'
import type { Feature } from '../types'

export function useFeatures() {
  return useQuery<Feature[]>({
    queryKey: queryKeys.features.list(),
    queryFn: () => featureClient.getAll(),
  })
}
