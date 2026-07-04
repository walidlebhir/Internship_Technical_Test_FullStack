import { apiClient } from '@/shared/services'
import { API_ENDPOINTS } from '@/shared/constants'
import type { EvaluationResult, EvaluateRequest } from '../types'

export const evaluationClient = {
  evaluate({ featureKey, userId, environment }: EvaluateRequest): Promise<EvaluationResult> {
    const params: Record<string, string> = {}
    if (userId) params.userId = userId
    if (environment) params.environment = environment
    return apiClient.get(API_ENDPOINTS.EVALUATION.EVALUATE(featureKey), { params })
  },
}
