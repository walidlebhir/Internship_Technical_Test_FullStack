import { useQuery } from '@tanstack/react-query'
import { domainClient } from '../api'
import { queryKeys } from '@/shared/utils'
import type { Domain } from '../types'

export function useDomain(id: string) {
  return useQuery<Domain>({
    queryKey: queryKeys.domains.detail(id),
    queryFn: () => domainClient.getById(id),
    enabled: !!id,
  })
}
