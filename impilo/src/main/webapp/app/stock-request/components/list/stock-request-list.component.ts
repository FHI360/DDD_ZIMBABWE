import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDrawer } from '@angular/material/sidenav';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { Subject } from 'rxjs';
import { OrgUnit } from '../../../stock/stock.service';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslocoService } from '@ngneat/transloco';
import { StockRequest, StockRequestService } from '../../stock-request.service';

@Component({
    selector: 'stocks-request',
    templateUrl: './stock-request-list.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class StocksRequestListComponent implements OnInit, OnDestroy {
    @ViewChild('matDrawer', {static: true}) matDrawer: MatDrawer;
    @ViewChild(MatPaginator, {static: true}) paginator: MatPaginator;
    dataSource: MatTableDataSource<StockRequest> | null;
    ofcadSites: OrgUnit[] = [];
    orgType: string
    site: OrgUnit;
    arvDrug = '';
    pageSize = 10;
    pageSizeOptions: number[] = [5, 10, 20];
    drawerMode: 'side' | 'over';
    private _unsubscribeAll: Subject<any> = new Subject<any>();

    columns: any[] = [
        {
            label: 'IMPILO.STOCK_REQUEST.LIST_PAGE.DATE',
            property: 'date',
            type: 'date',
            visible: true,
            cssClasses: ['font-medium']
        },
        {
            label: 'IMPILO.STOCK_REQUEST.LIST_PAGE.SITE',
            property: 'siteName',
            type: 'text',
            visible: true,
            cssClasses: ['font-medium']
        },
        {
            label: 'IMPILO.STOCK_REQUEST.LIST_PAGE.ARV_DRUG',
            property: 'arvDrug',
            type: 'text',
            visible: true,
            cssClasses: ['font-medium']
        },
        {
            label: 'IMPILO.STOCK_REQUEST.LIST_PAGE.BOTTLES',
            property: 'bottles',
            type: 'number',
            visible: true,
            cssClasses: ['font-medium']
        },
        {
            label: 'IMPILO.STOCK_REQUEST.LIST_PAGE.FULFILLED',
            property: 'fulfilled',
            type: 'number',
            visible: true,
            cssClasses: ['font-medium']
        },
        {label: 'IMPILO.STOCK_ISSUANCE.LIST_PAGE.ACTIONS', property: 'actions', type: 'button', visible: true}
    ];

    get visibleColumns() {
        return this.columns.filter(column => column.visible).map(column => column.property);
    }

    constructor(private _activatedRoute: ActivatedRoute,
                private _changeDetectorRef: ChangeDetectorRef,
                private _translocoService: TranslocoService,
                private _stockRequestService: StockRequestService,
                private _router: Router) {
        this.dataSource = new MatTableDataSource<StockRequest>();
    }

    ngOnInit(): void {
        this._activatedRoute.data.subscribe(({requests}) => {
            this.dataSource.data = requests.data;
            this.paginator.length = requests.totalSize;

            this._changeDetectorRef.markForCheck();
        });

        this._stockRequestService.ofcadSites().subscribe(res => {
            this.ofcadSites = res;
            this._changeDetectorRef.markForCheck();
        });

        this._stockRequestService.getAccount().subscribe(res => {
            this.orgType = res?.organisation?.type;

            this._changeDetectorRef.markForCheck();
        });
    }

    ngOnDestroy() {
        this._unsubscribeAll.next(null);
        this._unsubscribeAll.complete();
    }

    siteChanged(site: OrgUnit) {
        this.site = site;
        this.listRequests();
    }

    listRequests() {
        this._stockRequestService.list(this.site?.id, this.arvDrug, this.paginator.pageIndex, this.paginator.pageSize)
            .subscribe(res => {
                this.dataSource.data = res.data;
                this.paginator.length = res.totalSize;

                this._changeDetectorRef.markForCheck();
            });
    }

    page(event: PageEvent) {
        this.listRequests();
    }

    issueStock(requestId: any) {
        this._router.navigate(['stock-issuance', 'request', requestId])
    }

    trackByFn(index: number, item: any): any {
        return item.id || index;
    }
}
