import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { PatientListComponent } from './patient.list.component';
import { MatButtonModule } from '@angular/material/button';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatIconModule } from '@angular/material/icon';
import { MatDrawerToggleResult } from '@angular/material/sidenav';
import { Patient } from '../devolve.service';
import { Refill } from '../patient.service';
import { NgForOf, NgIf } from '@angular/common';
import { LuxonModule } from 'luxon-angular';
import { TranslocoModule, TranslocoService } from '@ngneat/transloco';
import { LocaleOptions } from 'luxon';

@Component({
    selector: 'patient-details',
    templateUrl: './patient.detail.component.html',
    standalone: true,
    imports: [
        MatButtonModule,
        MatTooltipModule,
        MatIconModule,
        RouterLink,
        NgIf,
        LuxonModule,
        TranslocoModule,
        NgForOf
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PatientDetailComponent implements OnInit {
    patient: Patient;
    refills: Refill[];
    config: LocaleOptions;

    constructor(private _activatedRoute: ActivatedRoute,
                private _changeDetectorRef: ChangeDetectorRef,
                private _devolveComponent: PatientListComponent,
                private _translocoService: TranslocoService) {
    }

    ngOnInit(): void {
        this._devolveComponent.matDrawer.open();
        this._activatedRoute.data.subscribe(({patient, refills}) => {
            this.patient = patient;
            this.refills = refills;

            this._changeDetectorRef.markForCheck();
        });

        this._translocoService.langChanges$.subscribe(lang=> {
            this.config = {
                locale: lang
            }

            this._changeDetectorRef.markForCheck();
        });
    }

    closeDrawer(): Promise<MatDrawerToggleResult> {
        return this._devolveComponent.matDrawer.close();
    }

    trackByFn(index: number, item: any): any {
        return item.id || index;
    }

}
