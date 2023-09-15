import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatPaginator, MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { ReactiveFormsModule, UntypedFormControl } from '@angular/forms';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { map, Subject, takeUntil } from 'rxjs';
import { ActivatedRoute, Router, RouterLink, RouterOutlet } from '@angular/router';
import { OrgUnit } from '../../stock/stock.service';
import { animate, state, style, transition, trigger } from '@angular/animations';
import { TranslocoModule, TranslocoService } from '@ngneat/transloco';
import { AccountService, HasAnyAuthorityDirective } from "@mattae/angular-shared";
import { MatDrawer, MatSidenavModule } from "@angular/material/sidenav";
import { MatSelectModule } from "@angular/material/select";
import { MatIconModule } from "@angular/material/icon";
import { MatInputModule } from "@angular/material/input";
import { NgClass, NgForOf, NgIf } from "@angular/common";
import { MatMenuModule } from '@angular/material/menu';
import { MatButtonModule } from '@angular/material/button';
import {Patient, PatientService} from "../patient.service";

@Component({
    selector: 'site-assignment',
    templateUrl: './patient.list.component.html',
    styleUrls: ['./patient.list.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: true,
    animations: [
        trigger('detailExpand', [
            state('collapsed', style({height: '0px', minHeight: '0'})),
            state('expanded', style({height: '*'})),
            transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
        ]),
    ],
    imports: [
        MatSidenavModule,
        RouterOutlet,
        TranslocoModule,
        MatSelectModule,
        MatIconModule,
        MatInputModule,
        NgForOf,
        MatTableModule,
        NgIf,
        ReactiveFormsModule,
        NgClass,
        MatPaginatorModule,
        MatMenuModule,
        MatButtonModule,
        RouterLink,
        HasAnyAuthorityDirective
    ]
})
export class PatientListComponent implements OnInit, OnDestroy {
    expandedElement: Patient | null | undefined;
    @ViewChild(MatPaginator, {static: true}) paginator: MatPaginator;
    @ViewChild('matDrawer', {static: true}) matDrawer: MatDrawer;
    searchInputControl: UntypedFormControl = new UntypedFormControl();
    dataSource: MatTableDataSource<Patient> | null;
    keyword = '';
    pageSize = 10;
    pageSizeOptions: number[] = [5, 10, 20];
    drawerMode: 'side' | 'over';
    private _unsubscribeAll: Subject<any> = new Subject<any>();
    ofcadSites: OrgUnit[] = [];
    category: boolean = undefined;

    columns: any[] = [
        {
            label: 'IMPILO.PATIENT_LIST.FACILITY',
            property: 'facilityName',
            type: 'text',
            visible: true,
            cssClasses: ['font-medium']
        },
        {
            label: 'IMPILO.PATIENT_LIST.GIVEN_NAME',
            property: 'givenName',
            type: 'text',
            visible: true,
            cssClasses: ['font-medium']
        },
        {
            label: 'IMPILO.PATIENT_LIST.FAMILY_NAME',
            property: 'familyName',
            type: 'text',
            visible: true,
            cssClasses: ['font-medium']
        },
        {
            label: 'IMPILO.PATIENT_LIST.ASSIGNED_REGIMEN',
            property: 'regimen',
            type: 'text',
            visible: true,
            cssClasses: ['font-medium']
        },
        {
            label: 'IMPILO.PATIENT_LIST.SEX',
            property: 'sex',
            type: 'text',
            visible: true,
            cssClasses: ['font-medium']
        },
        {
            label: 'IMPILO.PATIENT_LIST.HOSPITAL_NUMBER',
            property: 'hospitalNumber',
            type: 'text',
            visible: true,
            cssClasses: ['font-medium']
        },
        {
            label: 'IMPILO.PATIENT_LIST.SITE',
            property: 'site',
            type: 'text',
            visible: true,
            cssClasses: ['font-medium']
        }
    ];

    categories: any[] = [];

    get visibleColumns() {
        return this.columns.filter(column => column.visible).map(column => column.property);
    }

    constructor(private _activatedRoute: ActivatedRoute,
                private _changeDetectorRef: ChangeDetectorRef,
                private _translocoService: TranslocoService,
                private _devolveService: PatientService,
                public accountService: AccountService,
                private _router: Router) {
        this.dataSource = new MatTableDataSource<Patient>();
    }

    ngOnInit(): void {
        this.categories = [
            {state: 'true', name: this._translocoService.translate('IMPILO.PATIENT_LIST.DEVOLVED')},
            {state: 'false', name: this._translocoService.translate('IMPILO.PATIENT_LIST.NOT_DEVOLVED'),}
        ];
        if (this.accountService.hasAnyAuthority('Facility tasks')) {
            this.columns.push(
                {
                    label: 'IMPILO.PATIENT_LIST.ACTIONS',
                    property: 'actions',
                    type: 'actions',
                    visible: true,
                    cssClasses: ['font-medium']
                }
            );
        }
        this._activatedRoute.data.subscribe(({patients}) => {
            this.dataSource.data = patients.data;
            this.paginator.length = patients.totalSize;

            this._changeDetectorRef.markForCheck();
        });

        this.searchInputControl.valueChanges
            .pipe(
                takeUntil(this._unsubscribeAll),
                map(query => {
                        // Search
                        this.keyword = query;
                        this.listPatients();
                    }
                )
            )
            .subscribe();

        this._devolveService.ofcadSites().subscribe(res => {
            this.ofcadSites = res;
            this._changeDetectorRef.markForCheck();
        });

    }

    ngOnDestroy(): void {
        // Unsubscribe from all subscriptions
        this._unsubscribeAll.next(null);
        this._unsubscribeAll.complete();
    }

    listPatients() {
        this._devolveService.list(this.keyword, this.category, this.paginator.pageIndex, this.pageSize).subscribe(
            res => {
                this.dataSource.data = res.data;
                this.paginator.length = res.totalSize;

                this._changeDetectorRef.markForCheck();
            }
        )
    }

    categoryChanged(category: string) {
        if (category === 'true') {
            this.category = true;
        } else if (category === 'false') {
            this.category = false;
        } else {
            this.category = undefined;
        }

        this.listPatients();
    }

    siteChanged(patient: Patient, site?: OrgUnit) {
        if (site) {
            this._devolveService.devolvePatient(site.id, patient.id).subscribe(_ => this.listPatients());
        } else {
            this._devolveService.unDevolvePatient(patient.id).subscribe(_ => this.listPatients());
        }

    }

    page(event: PageEvent) {
        this.listPatients();
    }

    trackByFn(index: number, item: any): any {
        return item.id || index;
    }

    typeCompare = (t1: any, t2: any) => {
        return t1 === t2.name || t1.name === t2
    }
}
