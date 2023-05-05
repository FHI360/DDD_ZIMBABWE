import { Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';

export const APP_ROUTES: Routes = [
    {
        path: '',
        component: HomeComponent,
        pathMatch: 'full'
    },
    {
        path: 'site-assignments',
        loadChildren: () => import('./site-assignment/site-assignment.module')
            .then(m => m.SiteAssignmentModule)
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
    }
];
