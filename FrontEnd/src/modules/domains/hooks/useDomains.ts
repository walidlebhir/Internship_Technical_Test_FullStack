import { useQuery } from '@tanstack/react-query'
import { domainClient } from '../api'
import { queryKeys } from '@/shared/utils'
import type { Domain } from '../types'

export function useDomains() {
  return useQuery<Domain[]>({
    queryKey: queryKeys.domains.list(),
    queryFn: () => domainClient.getAll(),
  })
}
