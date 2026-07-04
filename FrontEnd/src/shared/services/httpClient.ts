import { httpClient } from '@/shared/lib/axios'
import type { AxiosRequestConfig } from 'axios'

export const apiClient = {
  get<TResponse>(url: string, config?: AxiosRequestConfig): Promise<TResponse> {
    return httpClient.get<TResponse>(url, config).then((res) => res.data)
  },

  post<TRequest, TResponse>(url: string, data?: TRequest, config?: AxiosRequestConfig): Promise<TResponse> {
    return httpClient.post<TResponse>(url, data, config).then((res) => res.data)
  },

  put<TRequest, TResponse>(url: string, data?: TRequest, config?: AxiosRequestConfig): Promise<TResponse> {
    return httpClient.put<TResponse>(url, data, config).then((res) => res.data)
  },

  patch<TRequest, TResponse>(url: string, data?: TRequest, config?: AxiosRequestConfig): Promise<TResponse> {
    return httpClient.patch<TResponse>(url, data, config).then((res) => res.data)
  },

  delete<TResponse>(url: string, config?: AxiosRequestConfig): Promise<TResponse> {
    return httpClient.delete<TResponse>(url, config).then((res) => res.data)
  },
}
