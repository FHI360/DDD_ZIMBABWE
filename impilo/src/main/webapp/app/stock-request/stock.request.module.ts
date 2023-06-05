import { inject, NgModule } from '@angular/core';
import { StockRequest, StockRequestService } from './stock-request.service';
import { StocksRequestListComponent } from './components/list/stock-request-list.component';
import { CommonModule } from '@angular/common';
import { MatSidenavModule } from '@angular/material/sidenav';
import { ActivatedRouteSnapshot, RouterModule, RouterStateSnapshot } from '@angular/router';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { TranslocoModule } from '@ngneat/transloco';
import { LuxonModule } from 'luxon-angular';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatOptionModule } from '@angular/material/core';
import { MatSelectModule } from '@angular/material/select';
import { Observable } from 'rxjs';
import { PagedResult } from '@mattae/angular-shared';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';

const resolveRequestList = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<PagedResult<StockRequest>> |
    Promise<PagedResult<StockRequest>> | PagedResult<StockRequest> => {
    return inject(StockRequestService).list();
}

@NgModule({
    imports: [
        CommonModule,
        MatSidenavModule,
        RouterModule.forChild([{
            path: '',
            component: StocksRequestListComponent,
            resolve: {
                requests: resolveRequestList
            }
        }]),
        MatPaginatorModule,
        MatIconModule,
        MatTableModule,
        TranslocoModule,
        LuxonModule,
        MatFormFieldModule,
        MatOptionModule,
        MatSelectModule,
        MatButtonModule,
        MatMenuModule,
    ],
    declarations: [
        StocksRequestListComponent
    ],
    providers: [
        StockRequestService
    ]
})
export class StockRequestModule {
}
