import type { RouteObject } from 'react-router-dom'
import { DashboardLayout, AuthLayout } from '@/shared/layouts'
import { DashboardPage } from '@/modules/dashboard/pages'
import { DomainListPage, DomainDetailPage } from '@/modules/domains/pages'
import { FeatureListPage, FeatureDetailPage } from '@/modules/features/pages'
import { StrategyListPage, StrategyDetailPage } from '@/modules/strategies/pages'
import { EvaluationPage } from '@/modules/evaluation/pages'
import { AuditListPage } from '@/modules/audit/pages'
import { SettingsPage } from '@/modules/settings/pages'

export const routeConfig: RouteObject[] = [
  {
    path: '/',
    element: <DashboardLayout />,
    children: [
      { index: true, element: <DashboardPage /> },
      { path: 'domains', element: <DomainListPage /> },
      { path: 'domains/:domainId', element: <DomainDetailPage /> },
      { path: 'features', element: <FeatureListPage /> },
      { path: 'features/:featureId', element: <FeatureDetailPage /> },
      { path: 'strategies', element: <StrategyListPage /> },
      { path: 'strategies/:strategyId', element: <StrategyDetailPage /> },
      { path: 'evaluation', element: <EvaluationPage /> },
      { path: 'audit', element: <AuditListPage /> },
      { path: 'settings', element: <SettingsPage /> },
    ],
  },
  {
    path: '/auth',
    element: <AuthLayout />,
    children: [
      { path: 'login', element: <div>Login Page</div> },
      { path: 'register', element: <div>Register Page</div> },
    ],
  },
]
