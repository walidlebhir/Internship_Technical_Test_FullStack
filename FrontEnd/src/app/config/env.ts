export const env = {
  apiBaseUrl: import.meta.env.VITE_API_BASE_URL as string,
  appName: import.meta.env.VITE_APP_NAME as string,
  appVersion: import.meta.env.VITE_APP_VERSION as string,
  nodeEnv: import.meta.env.MODE as string,
  isDev: import.meta.env.DEV,
  isProd: import.meta.env.PROD,
} as const
