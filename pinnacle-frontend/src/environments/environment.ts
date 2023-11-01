export const environment = {
  oauth: {
    allowedResourceUrls: ['http://localhost:8080', 'http://localhost:9000'],
    issuer: 'http://localhost:9000',
    redirectUri: 'http://localhost:8080/',
    clientId: 'pinnacle-oidc-client',
    responseType: 'code',
    scope: 'openid profile',
    requireHttps: false,
  }
};
