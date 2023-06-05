import { Component, inject, NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StockIssuanceListComponent } from './components/list/stock-issuance-list.component';
import { MatSidenavModule } from '@angular/material/sidenav';
import {
    ActivatedRouteSnapshot,
    Router,
    RouterLink,
    RouterModule,
    RouterOutlet,
    RouterStateSnapshot,
    Routes,
    UrlTree
} from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { TranslocoModule } from '@ngneat/transloco';
import { MatInputModule } from '@angular/material/input';
import { MatTableModule } from '@angular/material/table';
import { LuxonModule } from 'luxon-angular';
import { MatMenuModule } from '@angular/material/menu';
import { MatPaginatorModule } from '@angular/material/paginator';
import { ReactiveFormsModule } from '@angular/forms';
import { StockIssuance, StockIssuanceService } from './stock-issuance.service';
import { StockIssuanceDetailsComponent } from './components/details/stock-issuance.details.component';
import { catchError, EMPTY, Observable } from 'rxjs';
import { PagedResult, SharedModule } from '@mattae/angular-shared';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { StockRequest } from '../stock-request/stock-request.service';

@Component({
    selector: 'issuance-manager',
    template: '<router-outlet></router-outlet>'
})
export class IssuanceManagerComponent {

}

const resolveIssuance = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<StockIssuance> => {
    const id = route.params['id'] ? route.params['id'] : null;
    const router = inject(Router);
    // @ts-ignore
    return inject(StockIssuanceService).getById(id).pipe(
        catchError((err) => {
            router.navigateByUrl('/stock-issuance');
            return EMPTY;
        })
    );
}

const resolveRequest = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<StockRequest> => {
    const id = route.params['requestId'] ? route.params['requestId'] : null;
    const router = inject(Router);
    // @ts-ignore
    return inject(StockIssuanceService).getRequest(id).pipe(
        catchError((err) => {
            router.navigateByUrl('/stock-requests');
            return EMPTY;
        })
    );
}


const resolveIssuanceList = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<PagedResult<StockIssuance>> |
    Promise<PagedResult<StockIssuance>> | PagedResult<StockIssuance> => {
    return inject(StockIssuanceService).list();
}

const canDeactivateStockIssuanceDetails = (
    component: StockIssuanceDetailsComponent,
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
    if (!nextState.url.includes('/stock-issuance')) {
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

const ROUTES: Routes = [
    {
        path: '',
        component: IssuanceManagerComponent,
        children: [
            {
                path: '',
                component: StockIssuanceListComponent,
                resolve: {
                    list: resolveIssuanceList
                },
                data: {
                    title: 'IMPILO.TITLE.STOCK_ISSUANCE_LIST',
                },
                children: [
                    {
                        path: 'details/:id',
                        component: StockIssuanceDetailsComponent,
                        resolve: {
                            issuance: resolveIssuance
                        },
                        data: {
                            authorities: [],
                            title: 'IMPILO.TITLE.STOCK_ISSUANCE'
                        },
                        canDeactivate: [canDeactivateStockIssuanceDetails]
                    },
                    {
                        path: 'request/:requestId',
                        component: StockIssuanceDetailsComponent,
                        data: {
                            title: 'IMPILO.TITLE.ADD_STOCK_ISSUANCE'
                        },
                        resolve: {
                            request: resolveRequest
                        },
                        canDeactivate: [canDeactivateStockIssuanceDetails]
                    }
                ]
            }
        ]
    }
];


@NgModule({
    imports: [
        CommonModule,
        RouterModule.forChild(ROUTES),
        MatSidenavModule,
        RouterOutlet,
        RouterLink,
        MatIconModule,
        MatButtonModule,
        TranslocoModule,
        MatInputModule,
        MatTableModule,
        LuxonModule,
        MatMenuModule,
        MatPaginatorModule,
        ReactiveFormsModule,
        MatTooltipModule,
        SharedModule,
        MatSelectModule,
        MatDatepickerModule
    ],
    declarations: [
        IssuanceManagerComponent,
        StockIssuanceDetailsComponent,
        StockIssuanceListComponent
    ],
    providers: [
        StockIssuanceService
    ]
})
export class StockIssuanceModule {

}
