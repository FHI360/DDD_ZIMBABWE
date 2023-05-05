import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormGroup, UntypedFormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatDrawerToggleResult } from '@angular/material/sidenav';
import { catchError, EMPTY, map } from 'rxjs';
import { DateTime } from 'luxon';
import { FuseAlertType } from '@mattae/angular-shared';
import { StockIssuanceService } from '../../stock-issuance.service';
import { StockIssuanceListComponent } from '../list/stock-issuance-list.component';
import { OrgUnit, Stock } from '../../../stock/stock.service';

@Component({
    selector: 'stock-details',
    templateUrl: './stock-issuance.detail.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class StockIssuanceDetailsComponent implements OnInit {
    site: any;
    min = new Date();
    max = new Date();
    pathHasId = false;
    editMode: boolean = false;
    formGroup: FormGroup;
    showAlert = false;
    ofcadSites: OrgUnit[] = [];
    stocks: Stock[] = [];
    selectedStock: Stock;
    alert: { type: FuseAlertType; message: string } = {
        type: 'success',
        message: ''
    };

    orgType: string;

    constructor(private _stockService: StockIssuanceService,
                private _activatedRoute: ActivatedRoute,
                private _changeDetectorRef: ChangeDetectorRef,
                private fb: UntypedFormBuilder,
                private _router: Router,
                private _stockListComponent: StockIssuanceListComponent) {

        this.formGroup = fb.group({
            id: [],
            date: [{disabled: true, value: DateTime.now()}],
            bottles: [null, Validators.required],
            site: [null, Validators.required],
            stock: [null, Validators.required]
        });
    }

    ngOnInit(): void {
        this._stockListComponent.matDrawer.open();
        this.pathHasId = !!this._activatedRoute.snapshot.params['id'];
        this._activatedRoute.data.subscribe(({issuance}) => {
            console.log(issuance)
            if (issuance) {
                this.formGroup.patchValue(issuance);
            } else {
                this.editMode = true;
                this.site = {}
            }

            this._changeDetectorRef.markForCheck();
        });

        this._stockService.ofcadSites().subscribe(res => {
            this.ofcadSites = res;

            this._changeDetectorRef.markForCheck();
        });

        this._stockService.availableStock().subscribe(res => {
            this.stocks = res;
            this.stockSelected();

            this._changeDetectorRef.markForCheck();
        });
    }

    stockSelected() {
        this.selectedStock = this.stocks.find(stock => stock.id === this.formGroup.value['stock']['id']);
        this.selectedStock.balance = this.selectedStock.bottles - (this.selectedStock.issued || 0);
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

    typeCompare = (t1: any, t2: any) => t1.id ? t1.id === t2.id : t1 === t2


    save(): void {
        const issuance = this.formGroup.getRawValue();
        if (issuance.id) {
            this._stockService.update(issuance).pipe(
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
            if (!issuance.date) {
                issuance.date = DateTime.now();
            }
            this._stockService.save(issuance).pipe(
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
