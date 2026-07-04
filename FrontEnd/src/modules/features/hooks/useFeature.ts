import { useQuery } from '@tanstack/react-query'
import { featureClient } from '../api'
import { queryKeys } from '@/shared/utils'
import type { Feature } from '../types'

export function useFeature(id: number) {
  return useQuery<Feature>({
    queryKey: queryKeys.features.detail(String(id)),
    queryFn: () => featureClient.getById(id),
    enabled: !!id,
  })
}
