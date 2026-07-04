import commonEn from './locales/en/common.json'
import commonFr from './locales/fr/common.json'
import dashboardEn from './locales/en/dashboard.json'
import dashboardFr from './locales/fr/dashboard.json'
import domainsEn from './locales/en/domains.json'
import domainsFr from './locales/fr/domains.json'
import featuresEn from './locales/en/features.json'
import featuresFr from './locales/fr/features.json'
import strategiesEn from './locales/en/strategies.json'
import strategiesFr from './locales/fr/strategies.json'
import evaluationEn from './locales/en/evaluation.json'
import evaluationFr from './locales/fr/evaluation.json'
import auditEn from './locales/en/audit.json'
import auditFr from './locales/fr/audit.json'
import settingsEn from './locales/en/settings.json'
import settingsFr from './locales/fr/settings.json'

export const resources = {
  en: {
    common: commonEn,
    dashboard: dashboardEn,
    domains: domainsEn,
    features: featuresEn,
    strategies: strategiesEn,
    evaluation: evaluationEn,
    audit: auditEn,
    settings: settingsEn,
  },
  fr: {
    common: commonFr,
    dashboard: dashboardFr,
    domains: domainsFr,
    features: featuresFr,
    strategies: strategiesFr,
    evaluation: evaluationFr,
    audit: auditFr,
    settings: settingsFr,
  },
} as const
