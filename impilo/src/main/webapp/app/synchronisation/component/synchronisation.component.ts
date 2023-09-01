import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {MatButtonModule} from '@angular/material/button';
import {TranslocoModule, TranslocoService} from '@ngneat/transloco';
import {SynchronizationService} from '../synchronization.service';
import {FuseAlertComponent, FuseAlertType} from '@mattae/angular-shared';
import {catchError, EMPTY, finalize, map} from 'rxjs';
import {NgIf} from '@angular/common';
import {MatSidenavModule} from '@angular/material/sidenav';

@Component({
    selector: 'synchronization',
    templateUrl: './synchronisation.component.html',
    imports: [
        MatButtonModule,
        TranslocoModule,
        FuseAlertComponent,
        NgIf,
        MatSidenavModule
    ],
    standalone: true,
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [
        SynchronizationService
    ]
})
export class SynchronisationComponent implements OnInit {
    running = false;
    showAlert = false;
    alert: { type: FuseAlertType; message: string } = {
        type: 'success',
        message: ''
    };

    drawerMode: 'side' | 'over';

    constructor(private service: SynchronizationService, private _changeDetectorRef: ChangeDetectorRef,
                private _translocoService: TranslocoService) {
    }

    ngOnInit() {
    }

    syncEHR() {
        this.running = true;
        this.showAlert = false;
        this.service.syncEHR().pipe(
            map(res => {
                this.alert = {
                    type: 'success',
                    message: 'IMPILO.REPORT.MESSAGES.SUCCESS',
                };
                this.showAlert = true;
                this._changeDetectorRef.markForCheck();
            }),
            catchError(err => {
                this.showAlert = true;
                this.alert = {
                    type: 'error',
                    message: err.details?.message
                };
                this._changeDetectorRef.markForCheck();
                return EMPTY;
            }),
            finalize(() => {
                this.running = false;
            })
        ).subscribe();
    }

    syncCentralServer() {
        this.running = true;
        this.showAlert = false;
        this.service.syncEHR().pipe(
            map(res => {
                this.alert = {
                    type: 'success',
                    message: 'IMPILO.SYNC.MESSAGES.SUCCESS',
                };
                this.showAlert = true;
                this._changeDetectorRef.markForCheck();
            }),
            catchError((err: any) => {
                this.showAlert = true;
                this.alert = {
                    type: 'error',
                    message: `${this._translocoService.translate('IMPILO.SYNC.MESSAGES.ERROR')}: ${err.error.detail}`
                };
                this._changeDetectorRef.markForCheck();
                return EMPTY;
            }),
            finalize(() => {
                this.running = false;
            })
        ).subscribe();
    }
}
