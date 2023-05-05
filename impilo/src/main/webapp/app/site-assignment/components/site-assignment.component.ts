import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { UntypedFormControl } from '@angular/forms';
import { MatTableDataSource } from '@angular/material/table';
import { Patient, SiteAssignmentService } from '../site-assignment.service';
import { map, Subject, takeUntil } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import { OrgUnit } from '../../stock/stock.service';
import { animate, state, style, transition, trigger } from '@angular/animations';
import { TranslocoService } from '@ngneat/transloco';

@Component({
    selector: 'site-assignment',
    templateUrl: './site-assignment.component.html',
    styleUrls: ['./site-assignment.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    animations: [
        trigger('detailExpand', [
            state('collapsed', style({height: '0px', minHeight: '0'})),
            state('expanded', style({height: '*'})),
            transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
        ]),
    ],
})
export class SiteAssignmentComponent implements OnInit, OnDestroy {
    expandedElement: Patient | null | undefined;
    @ViewChild(MatPaginator, {static: true}) paginator: MatPaginator;
    searchInputControl: UntypedFormControl = new UntypedFormControl();
    dataSource: MatTableDataSource<Patient> | null;
    keyword = '';
    pageSize = 10;
    pageSizeOptions: number[] = [5, 10, 20];
    drawerMode: 'side' | 'over';
    private _unsubscribeAll: Subject<any> = new Subject<any>();
    ofcadSites: OrgUnit[] = [];
    category: boolean;

    columns: any[] = [
        {
            label: 'IMPILO.ASSIGNMENT.GIVEN_NAME',
            property: 'givenName',
            type: 'text',
            visible: true,
            cssClasses: ['font-medium']
        },
        {
            label: 'IMPILO.ASSIGNMENT.FAMILY_NAME',
            property: 'familyName',
            type: 'text',
            visible: true,
            cssClasses: ['font-medium']
        },
        {
            label: 'IMPILO.ASSIGNMENT.ASSIGNED_REGIMEN',
            property: 'regimen',
            type: 'text',
            visible: true,
            cssClasses: ['font-medium']
        },
        {
            label: 'IMPILO.ASSIGNMENT.SEX',
            property: 'sex',
            type: 'text',
            visible: true,
            cssClasses: ['font-medium']
        },
        {
            label: 'IMPILO.ASSIGNMENT.HOSPITAL_NUMBER',
            property: 'hospitalNumber',
            type: 'text',
            visible: true,
            cssClasses: ['font-medium']
        },
        {
            label: 'IMPILO.ASSIGNMENT.UNIQUE_ID',
            property: 'uniqueId',
            type: 'text',
            visible: true,
            cssClasses: ['font-medium']
        },
        {
            label: 'IMPILO.ASSIGNMENT.SITE',
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
                private _siteAssignmentService: SiteAssignmentService,
                private _router: Router) {
        this.dataSource = new MatTableDataSource<Patient>();
    }

    ngOnInit(): void {
        this.categories = [
            {state: 'true', name: this._translocoService.translate('IMPILO.ASSIGNMENT.ASSIGNED')},
            {state: 'false', name: this._translocoService.translate('IMPILO.ASSIGNMENT.UNASSIGNED'),}
        ];
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

        this._siteAssignmentService.ofcadSites().subscribe(res => {
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
        this._siteAssignmentService.list(this.keyword, this.category, this.paginator.pageIndex, this.pageSize).subscribe(
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
            this._siteAssignmentService.assignPatient(site.id, patient.id).subscribe(_=> this.listPatients());
        } else {
            this._siteAssignmentService.unassignPatient(patient.id).subscribe(_=> this.listPatients());
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
