import axios from 'axios';

// Always use relative /api paths: browser sees fe.test, Nginx forwards to Spring Boot.
export const api = axios.create({
  baseURL: '/api',
  withCredentials: true,
  withXSRFToken: true,
  xsrfCookieName: 'XSRF-TOKEN',
  xsrfHeaderName: 'X-XSRF-TOKEN',
});

// DEBUG ONLY: FE can read XSRF-TOKEN, but cannot read HttpOnly ACCESS_TOKEN.
const debug = import.meta.env.DEV;
function csrfCookie() {
  return document.cookie.split('; ').find(c => c.startsWith('XSRF-TOKEN='))?.slice('XSRF-TOKEN='.length) || null;
}
if (debug) {
  api.interceptors.request.use(config => {
    console.log(`[FE REQUEST] ${config.method?.toUpperCase()} ${config.baseURL}${config.url}`, {
      csrfCookie: csrfCookie(),
      csrfHeader: config.headers?.get?.('X-XSRF-TOKEN') ?? '(Axios may add XSRF header after interceptor)',
      jwtCookie: 'HttpOnly: cannot be read by JavaScript; inspect DevTools > Network > Request Headers',
    });
    return config;
  });
  api.interceptors.response.use(response => {
    console.log(`[FE RESPONSE] ${response.status} ${response.config.url}`, response.data);
    return response;
  }, error => {
    console.warn('[FE RESPONSE ERROR]', error.response?.status, error.config?.url, error.response?.data);
    return Promise.reject(error);
  });
}
export function errorMessage(error) {
  const data = error?.response?.data;
  return data?.error || data?.message || (error?.response?.status === 403
    ? '403 Forbidden — check CSRF token and cookie' : error.message || 'Request failed');
}
