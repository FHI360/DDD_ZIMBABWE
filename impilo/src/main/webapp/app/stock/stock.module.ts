import { Component, inject, NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Stock, StockService } from './stock.service';
import { StockDetailsComponent } from './components/details/stock.details.component';
import { StockListComponent } from './components/list/stock-list.component';
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
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { MatPaginatorModule } from '@angular/material/paginator';
import { TranslocoModule } from '@ngneat/transloco';
import { MatTableModule } from '@angular/material/table';
import { ReactiveFormsModule } from '@angular/forms';
import { LuxonModule } from 'luxon-angular';
import { MatTooltipModule } from '@angular/material/tooltip';
import {FuseAlertComponent, PagedResult} from '@mattae/angular-shared';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { catchError, EMPTY, Observable } from 'rxjs';
import { MatSortModule } from '@angular/material/sort';

@Component({
    selector: 'stock-manager',
    template: '<router-outlet></router-outlet>'
})
export class StockManagerComponent {

}

const resolveStock = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Stock> => {
    const id = route.params['id'] ? route.params['id'] : null;
    const router = inject(Router);
    // @ts-ignore
    return inject(StockService).getById(id).pipe(
        catchError((err) => {
            router.navigateByUrl('/stocks');
            return EMPTY;
        })
    );
}


const resolveStocks = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<PagedResult<Stock>> |
    Promise<PagedResult<Stock>> | PagedResult<Stock> => {
    return inject(StockService).list();
}

const canDeactivateStockDetails = (
    component: StockDetailsComponent,
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
    if (!nextState.url.includes('/stocks')) {
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
        component: StockManagerComponent,
        children: [
            {
                path: '',
                component: StockListComponent,
                resolve: {
                    stocks: resolveStocks
                },
                data: {
                    title: 'IMPILO.TITLE.STOCKS'
                },
                children: [
                    {
                        path: 'details/:id',
                        component: StockDetailsComponent,
                        resolve: {
                            stock: resolveStock
                        },
                        data: {
                            authorities: [],
                            title: 'IMPILO.TITLE.STOCK'
                        },
                        canDeactivate: [canDeactivateStockDetails]
                    },
                    {
                        path: 'details',
                        component: StockDetailsComponent,
                        data: {
                            title: 'IMPILO.TITLE.ADD_STOCK'
                        },
                        canDeactivate: [canDeactivateStockDetails]
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
        RouterLink,
        MatButtonModule,
        MatIconModule,
        MatInputModule,
        RouterOutlet,
        MatMenuModule,
        MatPaginatorModule,
        TranslocoModule,
        MatTableModule,
        ReactiveFormsModule,
        LuxonModule,
        MatTooltipModule,
        MatSelectModule,
        MatDatepickerModule,
        MatSortModule,
        FuseAlertComponent
    ],
    declarations: [
        StockDetailsComponent,
        StockListComponent,
        StockManagerComponent
    ],
    providers: [
        StockService
    ]
})
export class StockModule {

}
