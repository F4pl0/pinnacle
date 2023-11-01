import {Injectable} from '@angular/core';
import {OAuthService} from "angular-oauth2-oidc";
import {authCodeFlowConfig} from "../authCodeFlowConfig";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(
    private oauthService: OAuthService
  ) {
  }

  async init(): Promise<void> {
    this.oauthService.configure(authCodeFlowConfig);
    this.oauthService.setStorage(localStorage);
    this.oauthService.setupAutomaticSilentRefresh();
    await this.oauthService.loadDiscoveryDocumentAndTryLogin();
  }

  login(): void {
    this.oauthService.initCodeFlow();
    // this.oauthService.tryLogin({
    //   onTokenReceived: context => {
    //     console.log("onTokenReceived: ", context);
    //   }
    // })
  }
}
