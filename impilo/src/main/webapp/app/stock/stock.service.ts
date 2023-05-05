import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { DateTime } from 'luxon';
import { PagedResult } from '@mattae/angular-shared';
import { map } from 'rxjs';

export interface OrgUnit {
    id?: any;
    name?: string;
}

export interface Stock {
    id?: any,
    date?: DateTime;
    batchNo?: string;
    serialNo?: string;
    manufactureDate?: DateTime;
    expirationDate?: DateTime;
    regimen?: string;
    bottles?: number;
    issued?: number;
    balance?: number;
    facility?: string;
}

@Injectable()
export class StockService {
    private resourceUrl = '/api/impilo/stocks';

    constructor(private http: HttpClient) {

    }

    save(stock: Stock) {
        stock = this.convertFromClient(stock);
        return this.http.post<Stock>(this.resourceUrl, stock).pipe(
            map(res => this.convertFromServer(res))
        );
    }

    update(stock: Stock) {
        stock = this.convertFromClient(stock);
        return this.http.put<Stock>(this.resourceUrl, stock).pipe(
            map(res => this.convertFromServer(res))
        );
    }

    list(keyword?: string, start?: number, pageSize?: number) {
        const params = new HttpParams()
            .set('keyword', keyword ?? '')
            .set('start', (start ?? 0).toString())
            .set('pageSize', (pageSize ?? 10).toString());
        return this.http.get<PagedResult<Stock>>(this.resourceUrl, {params}).pipe(
            map(stocks => {
                stocks.data = stocks.data.map(stock => this.convertFromServer(stock));
                return stocks;
            })
        )
    }

    applicableRegimen() {
        return this.http.get<string[]>(`${this.resourceUrl}/applicable-regimen`)
    }

    getById(id: any) {
        return this.http.get<Stock>(`${this.resourceUrl}/${id}`).pipe(
            map(stock => this.convertFromServer(stock))
        )
    }

    deleteById(id: any) {
        return this.http.delete<void>(`${this.resourceUrl}/${id}`)
    }

    getAccount() {
        return this.http.get<any>('/api/account')
    }

    convertFromClient(stock: Stock) {
        return Object.assign({}, stock, {
            date: !!stock.date ? stock.date.toISODate() : null,
            manufactureDate: !!stock.manufactureDate ? stock.manufactureDate.toISODate() : null,
            expirationDate: !!stock.expirationDate ? stock.expirationDate.toISODate() : null
        });
    }

    convertFromServer(stock: Stock) {
        return Object.assign({}, stock, {
            date: !!stock.date ? DateTime.fromISO(stock.date as unknown as string) : null,
            manufactureDate: !!stock.manufactureDate ? DateTime.fromISO(stock.manufactureDate as unknown as string) : null,
            expirationDate: !!stock.expirationDate ? DateTime.fromISO(stock.expirationDate as unknown as string) : null,
            balance: stock.bottles - (stock.issued || 0),
            issued: stock.issued || 0
        })
    }
}
