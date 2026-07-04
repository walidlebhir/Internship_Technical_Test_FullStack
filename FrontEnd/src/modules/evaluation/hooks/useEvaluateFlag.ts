import { useMutation } from '@tanstack/react-query'
import { evaluationClient } from '../api'
import type { EvaluateRequest, EvaluationResult } from '../types'

export function useEvaluateFlag() {
  return useMutation<EvaluationResult, Error, EvaluateRequest>({
    mutationFn: (data) => evaluationClient.evaluate(data),
  })
}
