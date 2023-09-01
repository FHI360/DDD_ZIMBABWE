import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatButtonModule } from '@angular/material/button';
import { TranslocoModule, TranslocoService } from '@ngneat/transloco';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { DateTime } from 'luxon';
import { IndicatorService } from '../indicator.service';
import { FuseAlertComponent, FuseAlertType } from '@mattae/angular-shared';
import { catchError, EMPTY, finalize, map } from 'rxjs';
import { NgIf } from '@angular/common';
import { saveAs } from 'file-saver';
import { MatInputModule } from '@angular/material/input';
import { MatSidenavModule } from '@angular/material/sidenav';
import { DateAdapter } from '@angular/material/core';

@Component({
    selector: 'indicators',
    templateUrl: './indicator.component.html',
    imports: [
        MatDatepickerModule,
        MatButtonModule,
        TranslocoModule,
        ReactiveFormsModule,
        FuseAlertComponent,
        NgIf,
        MatInputModule,
        MatSidenavModule
    ],
    standalone: true,
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [
        IndicatorService
    ]
})
export class IndicatorComponent implements OnInit {
    running = false;
    showAlert = false;
    alert: { type: FuseAlertType; message: string } = {
        type: 'success',
        message: ''
    };
    today = new Date();
    range = new FormGroup({
        start: new FormControl<DateTime>(DateTime.now()),
        end: new FormControl<DateTime>(DateTime.now()),
    });
    drawerMode: 'side' | 'over';
    langConfig = {}

    constructor(private msrService: IndicatorService, private _translocoService: TranslocoService,
                private _changeDetectorRef: ChangeDetectorRef, private _dateAdapter: DateAdapter<any>,) {
    }

    ngOnInit() {
        this._translocoService.langChanges$.subscribe(lang => {
            this._dateAdapter.setLocale(lang);
            this.langConfig = {
                locale: lang
            }
        })
    }

    generate() {
        this.running = true;
        this.showAlert = false;
        const value = this.range.value;
        this.msrService.generate({start: value.start, end: value.end}).pipe(
            map(res => {
                this.alert = {
                    type: 'success',
                    message: 'IMPILO.REPORT.MESSAGES.SUCCESS',
                };
                this.showAlert = true;
                this._changeDetectorRef.markForCheck();
                const file = new File([res], name + 'Summary Report.xlsx', {type: 'application/octet-stream'});
                saveAs(file);
            }),
            catchError(err => {
                this.showAlert = true;
                this.alert = {
                    type: 'error',
                    message: 'IMPILO.REPORT.MESSAGES.ERROR'
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
