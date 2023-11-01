import {AuthConfig} from 'angular-oauth2-oidc';
import {environment} from "../../environments/environment";

export const authCodeFlowConfig: AuthConfig = {
  // Url of the Identity Provider
  issuer: environment.oauth.issuer,
  redirectUri: environment.oauth.redirectUri,
  clientId: environment.oauth.clientId,
  responseType: environment.oauth.responseType,
  scope: environment.oauth.scope,
  requireHttps: environment.oauth.requireHttps,
  showDebugInformation: true,
};
