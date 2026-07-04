import axios from 'axios'
import { env } from '@/app/config'

export const httpClient = axios.create({
  baseURL: env.apiBaseUrl,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 15_000,
})

httpClient.interceptors.request.use(
  (config) => {
    // TODO: Attach auth token from auth service
    return config
  },
  (error) => Promise.reject(error),
)

httpClient.interceptors.response.use(
  (response) => response,
  (error) => {
    // TODO: Handle 401 -> logout, 403 -> redirect, network errors, etc.
    return Promise.reject(error)
  },
)
