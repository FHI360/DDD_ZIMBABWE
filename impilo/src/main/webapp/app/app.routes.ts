import {Routes} from '@angular/router';
import {HomeComponent} from './home/home.component';

export const APP_ROUTES: Routes = [
    {
        path: '',
        component: HomeComponent,
        pathMatch: 'full'
    },
    {
        path: 'site-assignments',
        loadChildren: () => import('./devolve/devolve.module')
            .then(m => m.DevolveModule)
    },
    {
        path: 'stocks',
        loadChildren: () => import('./stock/stock.module')
            .then(m => m.StockModule)
    },
    {
        path: 'stock-issuance',
        loadChildren: () => import('./stock-issuance/stock-issuance.module')
            .then(m => m.StockIssuanceModule)
    },
    {
        path: 'stock-requests',
        loadChildren: () => import('./stock-request/stock.request.module')
            .then(m => m.StockRequestModule)
    },
    {
        path: 'reports',
        loadChildren: () => import('./reporting/indicator/indicator.routing')
    },
    {
        path: 'sync',
        loadChildren: () => import('./synchronisation/synchronization.routing')
    }
];
