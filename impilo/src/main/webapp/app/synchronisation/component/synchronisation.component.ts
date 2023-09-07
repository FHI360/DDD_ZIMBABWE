import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {MatButtonModule} from '@angular/material/button';
import {TranslocoModule, TranslocoService} from '@ngneat/transloco';
import {SynchronizationService} from '../synchronization.service';
import {FuseAlertComponent, FuseAlertType} from '@mattae/angular-shared';
import {catchError, EMPTY, finalize, map} from 'rxjs';
import {NgIf} from '@angular/common';
import {MatSidenavModule} from '@angular/material/sidenav';
import { FormBuilder, FormControl, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';

@Component({
    selector: 'synchronization',
    templateUrl: './synchronisation.component.html',
    imports: [
        MatButtonModule,
        TranslocoModule,
        FuseAlertComponent,
        NgIf,
        MatSidenavModule,
        FormsModule,
        MatDatepickerModule,
        MatFormFieldModule,
        ReactiveFormsModule,
        MatInputModule,
        MatIconModule,
        MatTooltipModule
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

    intervalCtl: FormControl = new FormControl(null, [Validators.min(300), Validators.required]);
    scheduling = false;

    drawerMode: 'side' | 'over';

    constructor(private _service: SynchronizationService,
                private _changeDetectorRef: ChangeDetectorRef,
                private _translocoService: TranslocoService) {
    }

    ngOnInit() {
    }

    syncEHR() {
        this.running = true;
        this.showAlert = false;
        this._service.syncEHR().pipe(
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
                    message: `${this._translocoService.translate('IMPILO.SYNC.MESSAGES.ERROR')}: ${this._translocoService.translate(err.error.detail)}`
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
        this._service.syncCentralServer().pipe(
            map(res => {
                this.alert = {
                    type: 'success',
                    message: 'IMPILO.SYNC.MESSAGES.SUCCESS',
                };
            }),
            catchError((err: any) => {
                this.alert = {
                    type: 'error',
                    message: `${this._translocoService.translate('IMPILO.SYNC.MESSAGES.ERROR')}: ${err.error.detail}`
                };
                return EMPTY;
            }),
            finalize(() => {
                this.running = false;
                this.showAlert = true;
                this._changeDetectorRef.markForCheck();
            })
        ).subscribe();
    }

    scheduleSync() {
        this.scheduling = true;
        this._service.scheduleSync(this.intervalCtl.value).pipe(
            map(_=> {
                this.intervalCtl.patchValue(null);
                this.alert = {
                    type: 'success',
                    message: 'IMPILO.SYNC.SCHEDULE.SUCCESS'
                };
            }),
            catchError((err: any) => {
                this.alert = {
                    type: 'error',
                    message:'IMPILO.SYNC.SCHEDULE.ERROR'
                };
                return EMPTY;
            }),
            finalize(() => {
                this.scheduling = false;
                this.showAlert = true;
                this._changeDetectorRef.markForCheck();
            })
        ).subscribe()
    }
}
