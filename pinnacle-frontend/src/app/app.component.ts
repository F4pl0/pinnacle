import {Component} from '@angular/core';
import {AuthService} from "./auth/services/auth.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'pinnacle-frontend';

  constructor(
    private authService: AuthService
  ) {
  }

  async ngOnInit(): Promise<void> {
    await this.authService.init();

  }

  login() {
    this.authService.login();
  }
}
