import { Routes } from '@angular/router';
import { IndicatorComponent } from './component/indicator.component';
import { ENVIRONMENT_INITIALIZER, inject } from '@angular/core';
import { StylesheetService } from '@mattae/angular-shared';
import { environment } from '../../../environments/environment';

export default [
    {
        path: '',
        providers: [
            {
                provide: ENVIRONMENT_INITIALIZER,
                multi: true,
                useValue() {
                    inject(StylesheetService).loadStylesheet(environment.production ? '/js/impilo-gateway/styles.css' : 'http://localhost:5000/styles.css');
                }
            }
        ],
        component: IndicatorComponent
    }
] as Routes
