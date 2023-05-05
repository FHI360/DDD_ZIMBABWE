import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { DateTime } from 'luxon';
import { PagedResult } from '@mattae/angular-shared';
import { map } from 'rxjs';
import { OrgUnit, Stock } from '../stock/stock.service';

export interface StockIssuance {
    id?: any,
    date?: DateTime;
    manufactureDate?: DateTime;
    expirationDate?: DateTime;
    regimen?: string;
    issued?: number;
    acknowledged?: boolean;
    site?: OrgUnit;
    facility?: OrgUnit;
    stock?: Stock
    ofcadSite: string
}

@Injectable()
export class StockIssuanceService {
    private resourceUrl = '/api/impilo/stock-issuance';

    constructor(private http: HttpClient) {

    }

    save(stock: StockIssuance) {
        stock = this.convertFromClient(stock);
        return this.http.post<StockIssuance>(this.resourceUrl, stock).pipe(
            map(res => this.convertFromServer(res))
        );
    }

    update(stock: StockIssuance) {
        stock = this.convertFromClient(stock);
        return this.http.put<StockIssuance>(this.resourceUrl, stock).pipe(
            map(res => this.convertFromServer(res))
        );
    }

    list(keyword?: string, start?: number, pageSize?: number) {
        const params = new HttpParams()
            .set('keyword', keyword ?? '')
            .set('start', (start ?? 0).toString())
            .set('pageSize', (pageSize ?? 10).toString());
        return this.http.get<PagedResult<StockIssuance>>(this.resourceUrl, {params}).pipe(
            map(stocks => {
                stocks.data = stocks.data.map(stock => this.convertFromServer(stock));
                return stocks;
            })
        )
    }

    getById(id: any) {
        return this.http.get<StockIssuance>(`${this.resourceUrl}/${id}`).pipe(
            map(stock => this.convertFromServer(stock))
        )
    }

    ofcadSites() {
        return this.http.get<OrgUnit[]>('/api/impilo/ofcad-sites')
    }

    availableStock() {
        return this.http.get<Stock[]>(`${this.resourceUrl}/available-stock`).pipe(
            map(stocks => stocks.map(stock => {
                stock.expirationDate = stock.expirationDate ?
                    DateTime.fromISO(stock.expirationDate as unknown as string) : null;
                stock.manufactureDate = stock.manufactureDate ?
                    DateTime.fromISO(stock.manufactureDate as unknown as string) : null;
                return stock;
            }))
        )
    }

    getAccount() {
        return this.http.get<any>('/api/account')
    }

    deleteById(id: any) {
        return this.http.delete<void>(`${this.resourceUrl}/${id}`)
    }

    convertFromClient(stock: StockIssuance) {
        return Object.assign({}, stock, {
            date: !!stock.date ? stock.date.toISODate() : null
        });
    }

    convertFromServer(issuance: StockIssuance) {
        return Object.assign({}, issuance, {
            date: !!issuance.date ? DateTime.fromISO(issuance.date as unknown as string) : null,
            manufactureDate: !!issuance.stock.manufactureDate ? DateTime.fromISO(issuance.stock.manufactureDate as unknown as string) : null,
            expirationDate: !!issuance.stock.expirationDate ? DateTime.fromISO(issuance.stock.expirationDate as unknown as string) : null,
            facility: issuance.stock.facility,
            issued: issuance.stock.issued || 0,
            regimen: issuance.stock.regimen,
            batchNo: issuance.stock.batchNo,
            ofcadSite: issuance.site.name
        })
    }
}
