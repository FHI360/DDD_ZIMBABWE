import { inject, NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SiteAssignmentComponent } from './components/site-assignment.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { LuxonModule } from 'luxon-angular';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatTableModule } from '@angular/material/table';
import {
    ActivatedRouteSnapshot,
    RouterLink,
    RouterModule,
    RouterOutlet,
    RouterStateSnapshot,
    Routes
} from '@angular/router';
import { TranslocoModule } from '@ngneat/transloco';
import { MatSelectModule } from '@angular/material/select';
import { Observable } from 'rxjs';
import { PagedResult } from '@mattae/angular-shared';
import { Patient, SiteAssignmentService } from './site-assignment.service';

const resolvePatients = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<PagedResult<Patient>> |
    Promise<PagedResult<Patient>> | PagedResult<Patient> => {
    return inject(SiteAssignmentService).list();
}

const ROUTES: Routes = [
    {
        path: '',
        children: [
            {
                path: '',
                component: SiteAssignmentComponent,
                resolve: {
                    patients: resolvePatients
                },
                data: {
                    title: 'IMPILO.TITLE.PATIENTS'
                }
            }
        ]
    }
]

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        RouterModule.forChild(ROUTES),
        LuxonModule,
        MatButtonModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
        MatMenuModule,
        MatPaginatorModule,
        MatSidenavModule,
        MatTableModule,
        RouterOutlet,
        TranslocoModule,
        RouterLink,
        ReactiveFormsModule,
        MatSelectModule
    ],
    declarations: [
        SiteAssignmentComponent
    ],
    providers: [
        SiteAssignmentService
    ]
})
export class SiteAssignmentModule {

}
