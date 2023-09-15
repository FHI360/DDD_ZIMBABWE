import { Component, ENVIRONMENT_INITIALIZER, inject } from '@angular/core';
import { PatientListComponent } from './components/patient.list.component';
import { ActivatedRouteSnapshot, Router, RouterOutlet, RouterStateSnapshot, Routes, UrlTree } from '@angular/router';
import { catchError, EMPTY, Observable } from 'rxjs';
import { canMatch, PagedResult, StylesheetService } from '@mattae/angular-shared';
import { PatientDetailComponent } from './components/patient.detail.component';
import {Patient, PatientService, Refill } from './patient.service';

@Component({
    selector: 'patient-management',
    template: '<router-outlet></router-outlet>',
    standalone: true,
    imports: [
        RouterOutlet
    ]
})
export class PatientManagementComponent {
}

const resolvePatients = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<PagedResult<Patient>> |
    Promise<PagedResult<Patient>> | PagedResult<Patient> => {
    return inject(PatientService).list();
}

const resolvePatient = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Patient> |
    Promise<Patient> | Patient => {
    const id = route.params['id'] ? route.params['id'] : null;
    const router = inject(Router);
    return inject(PatientService).getPatientById(id).pipe(
        catchError(err => {
            router.navigateByUrl('/patients');
            return EMPTY;
        })
    );
}

const resolveRefills = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Refill[]> |
    Promise<Refill[]> | Refill[] => {
    const id = route.params['id'] ? route.params['id'] : null;
    return inject(PatientService).getRefillsByPatient(id);
}

const canDeactivatePatientDetails = (
    component: PatientDetailComponent,
    currentRoute: ActivatedRouteSnapshot,
    currentState: RouterStateSnapshot,
    nextState: RouterStateSnapshot
): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree => {
    // Get the next route
    let nextRoute: ActivatedRouteSnapshot = nextState.root;
    while (nextRoute.firstChild) {
        nextRoute = nextRoute.firstChild;
    }

    // If the next state doesn't contain '/plugins'
    // it means we are navigating away from the
    // plugin manager app
    if (!nextState.url.includes('/patients')) {
        // Let it navigate
        return true;
    }

    // If we are navigating to another plugin...
    if (nextState.url.includes('/details')) {
        // Just navigate
        return true;
    }
    // Otherwise...
    else {
        // Close the drawer first, and then navigate
        return component.closeDrawer().then(() => true);
    }
}

export default [
    {
        path: '',
        component: PatientManagementComponent,
        providers: [
            {
                provide: ENVIRONMENT_INITIALIZER,
                multi: true,
                useValue() {
                    inject(StylesheetService).loadStylesheet('/js/impilo-gateway/styles.css');
                }
            },
            PatientService
        ],
        children: [
            {
                path: '',
                component: PatientListComponent,
                resolve: {
                    patients: resolvePatients
                },
                data: {
                    title: 'IMPILO.TITLE.PATIENTS'
                },
                canMatch: [canMatch],
                children: [
                    {
                        path: 'details/:id',
                        component: PatientDetailComponent,
                        resolve: {
                            patient: resolvePatient,
                            refills: resolveRefills
                        },
                        data: {
                            title: 'IMPILO.TITLE.PATIENT_DETAILS'
                        },
                        canMatch: [canMatch],
                        canDeactivate: [canDeactivatePatientDetails]
                    }
                ]
            }
        ]
    }
] as Routes

