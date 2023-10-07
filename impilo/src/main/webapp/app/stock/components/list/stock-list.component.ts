import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { map, Subject, takeUntil } from 'rxjs';
import { MatDrawer } from '@angular/material/sidenav';
import { FuseMediaWatcherService } from '@mattae/angular-shared';
import { UntypedFormControl } from '@angular/forms';
import { Stock, StockService } from '../../stock.service';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';

@Component({
    selector: 'stock-list',
    templateUrl: './stock-list.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class StockListComponent implements OnInit, OnDestroy {
    @ViewChild('matDrawer', {static: true}) matDrawer: MatDrawer;
    @ViewChild(MatPaginator, {static: true}) paginator: MatPaginator;
    searchInputControl: UntypedFormControl = new UntypedFormControl();
    dataSource: MatTableDataSource<Stock> | null;
    selectedSite: any;
    keyword = '';
    pageSize = 10;
    pageSizeOptions: number[] = [5, 10, 20];
    drawerMode: 'side' | 'over';
    private _unsubscribeAll: Subject<any> = new Subject<any>();
    orgType: string;

    columns: any[] = [
        {
            label: 'IMPILO.STOCK.LIST_PAGE.DATE',
            property: 'date',
            type: 'datetime',
            visible: true,
            cssClasses: ['font-medium']
        },
        {
            label: 'IMPILO.STOCK.LIST_PAGE.FACILITY',
            property: 'facility',
            type: 'text',
            visible: true,
            cssClasses: ['font-medium']
        },
        {
            label: 'IMPILO.STOCK.LIST_PAGE.REGIMEN',
            property: 'regimen',
            type: 'text',
            visible: true,
            cssClasses: ['font-medium']
        },
        {
            label: 'IMPILO.STOCK.LIST_PAGE.BOTTLES',
            property: 'bottles',
            type: 'number',
            visible: true,
            cssClasses: ['font-medium']
        },
        {
            label: 'IMPILO.STOCK.LIST_PAGE.ISSUED',
            property: 'issued',
            type: 'number',
            visible: true,
            cssClasses: ['font-medium']
        },
        {
            label: 'IMPILO.STOCK.LIST_PAGE.BALANCE',
            property: 'balance',
            type: 'number',
            visible: true,
            cssClasses: ['font-medium']
        },
        {
            label: 'IMPILO.STOCK.LIST_PAGE.BATCH_NO',
            property: 'batchNo',
            type: 'text',
            visible: true,
            cssClasses: ['font-medium']
        },
        {
            label: 'IMPILO.STOCK.LIST_PAGE.EXPIRATION_DATE',
            property: 'expirationDate',
            type: 'date',
            visible: true,
            cssClasses: ['font-medium']
        },
        {label: 'IMPILO.STOCK.LIST_PAGE.ACTIONS', property: 'actions', type: 'button', visible: true}
    ];

    get visibleColumns() {
        return this.columns.filter(column => column.visible).map(column => column.property);
    }

    constructor(private _stockService: StockService,
                private _activatedRoute: ActivatedRoute,
                private _changeDetectorRef: ChangeDetectorRef,
                private _router: Router,
                private _mediaWatcherService: FuseMediaWatcherService) {
        this.dataSource = new MatTableDataSource();
    }

    ngOnInit(): void {
        // Subscribe to MatDrawer opened change
        this.matDrawer.openedChange.subscribe((opened) => {
            if (!opened) {
                // Remove the selected contact when drawer closed
                this.listStock();
                this.selectedSite = null
                // Mark for check
                this._changeDetectorRef.markForCheck();
            }
        });

        // Subscribe to media changes
        this._mediaWatcherService.onMediaChange$
            .pipe(takeUntil(this._unsubscribeAll))
            .subscribe(({matchingAliases}) => {

                // Set the drawerMode if the given breakpoint is active
                if (matchingAliases.includes('lg')) {
                    this.drawerMode = 'side';
                } else {
                    this.drawerMode = 'over';
                }

                // Mark for check
                this._changeDetectorRef.markForCheck();
            });
        this._activatedRoute.data.subscribe(({stocks}) => {
            this.dataSource.data = stocks.data;
            this.paginator.length = stocks.totalSize;

            this._changeDetectorRef.markForCheck();
        });

        this.searchInputControl.valueChanges
            .pipe(
                takeUntil(this._unsubscribeAll),
                map(query =>
                    // Search
                    this.listStock(query)
                )
            )
            .subscribe();

        this.matDrawer.openedChange.subscribe(opened => {
            if (!opened) {
                this.listStock();
                this._changeDetectorRef.markForCheck();
            }
        });

        this._stockService.getAccount().subscribe(res => {
            this.orgType = res?.organisation?.type;

            this._changeDetectorRef.markForCheck();
        });
    }

    ngOnDestroy(): void {
        // Unsubscribe from all subscriptions
        this._unsubscribeAll.next(null);
        this._unsubscribeAll.complete();
    }

    onBackdropClicked(): void {
        // Go back to the list
        this._router.navigate(['./'], {relativeTo: this._activatedRoute});

        // Mark for check
        this._changeDetectorRef.markForCheck();
    }


    listStock(keyword?: any): void {
        this._stockService.list(keyword, this.paginator.pageIndex, this.paginator.pageSize).subscribe(res => {
            this.dataSource.data = res.data;
            this.paginator.length = res.totalSize;

            this._changeDetectorRef.markForCheck();
        });
    }

    page(event: PageEvent) {
        this.listStock();
    }

    trackByFn(index: number, item: any): any {
        return item.id || index;
    }

    deleteStock(stock: Stock) {
        this._stockService.deleteById(stock.id).subscribe()
    }

    editStock(stock: Stock) {
        this._router.navigate(['details', stock.id], {relativeTo: this._activatedRoute})
    }
}
