import { createBrowserRouter, RouterProvider } from 'react-router-dom'
import { routeConfig } from './routes'

const router = createBrowserRouter(routeConfig)

export function AppRouter() {
  return <RouterProvider router={router} />
}
