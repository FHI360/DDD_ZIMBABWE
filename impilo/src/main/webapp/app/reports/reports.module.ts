import { Component, NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
    selector: 'report-component',
    template: ''
})
export class ReportComponent {
}

@NgModule({
    imports: [
        CommonModule,
        RouterModule.forChild([
            {
                path: '',
                component: ReportComponent
            }
        ])
    ],
    declarations: [
        ReportComponent
    ]
})
export class ReportsModule {
}
