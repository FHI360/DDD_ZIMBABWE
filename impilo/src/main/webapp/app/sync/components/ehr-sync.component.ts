import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { SyncService } from '../sync.service';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { catchError, EMPTY, finalize, map } from 'rxjs';
import { FuseAlertModule, FuseAlertType, shake } from '@mattae/angular-shared';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { TranslocoModule, TranslocoService } from '@ngneat/transloco';
import { MatSidenavModule } from '@angular/material/sidenav';

@Component({
    selector: 'ehr-sync',
    templateUrl: './ehr-sync.component.html',
    standalone: true,
    animations: [shake],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        CommonModule,
        ReactiveFormsModule,
        FuseAlertModule,
        MatInputModule,
        MatIconModule,
        MatButtonModule,
        MatProgressSpinnerModule,
        TranslocoModule,
        MatSidenavModule
    ]
})
export class EhrSyncComponent implements OnInit {
    showAlert = false;
    syncing: boolean = false;
    drawerMode: 'side' | 'over';
    formGroup: FormGroup;
    alert: { type: FuseAlertType; message: string } = {
        type: 'success',
        message: ''
    };

    constructor(private fb: FormBuilder,
                private syncService: SyncService,
                private translocoService: TranslocoService,
                private cdr: ChangeDetectorRef) {
        this.formGroup = fb.group({
            username: ['', Validators.required],
            password: ['', Validators.required]
        })
    }

    ngOnInit(): void {
    }

    sync(): void {
        const details = this.formGroup.value;
        this.syncing = true;
        this.syncService.sync(details.username, details.password).pipe(
            map(res => {
                this.showAlert = true;
                if (res) {
                    this.alert.message = this.translocoService.translate('IMPILO.SYNC.SUCCESS');
                    this.alert.type = 'success';
                } else {
                    this.alert.message = this.translocoService.translate('IMPILO.SYNC.ERROR');
                    this.alert.type = 'error';
                }
            }),
            catchError((err) => {
                this.showAlert = true;
                this.alert.message = this.translocoService.translate('IMPILO.SYNC.ERROR');
                this.alert.type = 'error';
                if (err.status === 401) {
                    this.alert.message = this.translocoService.translate('IMPILO.SYNC.AUTHORIZATION_ERROR');
                }
                return EMPTY;
            }),
            finalize(() => {
                this.syncing = false;
                this.cdr.markForCheck();
            })
        ).subscribe();
    }
}
