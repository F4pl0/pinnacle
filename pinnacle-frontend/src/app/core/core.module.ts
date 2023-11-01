import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {CoreRoutingModule} from './core-routing.module';
import {HomeComponent} from './components/home/home.component';
import {MatButtonModule} from "@angular/material/button";


@NgModule({
  declarations: [
    HomeComponent
  ],
    imports: [
        CommonModule,
        CoreRoutingModule,
        MatButtonModule
    ]
})
export class CoreModule { }
