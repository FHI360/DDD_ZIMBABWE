import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormGroup, UntypedFormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatDrawerToggleResult } from '@angular/material/sidenav';
import { StockListComponent } from '../list/stock-list.component';
import { catchError, EMPTY, map } from 'rxjs';
import { StockService } from '../../stock.service';
import { DateTime } from 'luxon';
import { FuseAlertType } from '@mattae/angular-shared';
import { TranslocoService } from '@ngneat/transloco';
import { DateAdapter } from '@angular/material/core';

@Component({
    selector: 'stock-details',
    templateUrl: './stock.detail.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class StockDetailsComponent implements OnInit {
    site: any;
    min = new Date();
    max = new Date();
    pathHasId = false;
    editMode: boolean = false;
    formGroup: FormGroup;
    showAlert = false;
    regimen: string[] = [];
    alert: { type: FuseAlertType; message: string } = {
        type: 'success',
        message: ''
    };
    langKey: any = {};

    constructor(private _stockService: StockService,
                private _activatedRoute: ActivatedRoute,
                private _changeDetectorRef: ChangeDetectorRef,
                private fb: UntypedFormBuilder,
                private _router: Router,
                private _dateAdapter: DateAdapter<any>,
                private _translocoService: TranslocoService,
                private _stockListComponent: StockListComponent) {

        this.formGroup = fb.group({
            id: [],
            date: [{disabled: true, value: DateTime.now()}],
            regimen: [null, Validators.required],
            bottles: [null, Validators.required],
            serialNo: ['', Validators.required],
            batchNo: ['', Validators.required],
            manufactureDate: [],
            expirationDate: [],
            batchIssuanceId: []
        });
    }

    ngOnInit(): void {
        this._stockListComponent.matDrawer.open();
        this.pathHasId = !!this._activatedRoute.snapshot.params['id'];
        this._activatedRoute.data.subscribe(({stock}) => {
            if (stock) {
                this.formGroup.patchValue(stock);
            } else {
                this.editMode = true;
                this.site = {}
            }

            this._changeDetectorRef.markForCheck();
        });

        this._stockService.applicableRegimen().subscribe(res => {
            this.regimen = res;
            this._changeDetectorRef.markForCheck();
        });

        this._translocoService.langChanges$.subscribe(lang => {
            this.langKey.locale = lang;
            this._dateAdapter.setLocale(lang);
            this._changeDetectorRef.markForCheck();
        })
    }

    closeDrawer(): Promise<MatDrawerToggleResult> {
        return this._stockListComponent.matDrawer.close();
    }

    toggleEditMode(editMode: boolean | null = null): void {
        if (editMode === null) {
            this.editMode = !this.editMode;
        } else {
            this.editMode = editMode;
        }

        this._changeDetectorRef.markForCheck();
    }

    activate() {
        this.formGroup.patchValue({active: true});
        this.save();
    }

    typeCompare = (t1: any, t2: any) => t1 === t2


    save(): void {
        const stock = this.formGroup.getRawValue();
        if (stock.id) {
            this._stockService.update(stock).pipe(
                map(res => {
                    this.editMode = false;
                    this.formGroup.patchValue(res);
                    this._changeDetectorRef.markForCheck();
                }),
                catchError(err => {
                    return EMPTY
                })
            ).subscribe()
        } else {
            if (!stock.date) {
                stock.date = DateTime.now();
            }
            this._stockService.save(stock).pipe(
                map(res => {
                    this.editMode = false;
                    this.formGroup.patchValue(res);
                    this._changeDetectorRef.markForCheck();
                }),
                catchError(err => {
                    return EMPTY
                })
            ).subscribe()
        }
    }

}
