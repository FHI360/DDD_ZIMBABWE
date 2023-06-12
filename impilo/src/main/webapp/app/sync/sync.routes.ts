import { Routes } from "@angular/router";
import { EhrSyncComponent } from './components/ehr-sync.component';

export default [
    {
        path: '',
        children: [
            {
                path: '',
                component: EhrSyncComponent
            },
        ],
    },
] as Routes;
