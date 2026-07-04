export interface EvaluationResult {
  featureKey: string
  userId: string
  enabled: boolean
}

export interface EvaluateRequest {
  featureKey: string
  userId?: string
  environment?: string
}
