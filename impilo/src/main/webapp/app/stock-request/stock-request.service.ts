import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { PagedResult } from '@mattae/angular-shared';
import { map, Observable } from 'rxjs';
import { DateTime } from 'luxon';
import { OrgUnit } from '../stock/stock.service';

export interface StockRequest {
    date: DateTime;
    bottles: number;
    arvDrug: string;
    site: OrgUnit;
    siteName: string;
    fulfilled: number;
}

@Injectable()
export class StockRequestService {
    private resourceUrl = '/api/impilo/inventory/request';

    constructor(private http: HttpClient) {
    }
    list(siteCode?: string, arvDrug?: string, start?: number, pageSize?: number): Observable<PagedResult<StockRequest>> {
        const params = new HttpParams()
            .set('siteId', siteCode ?? '')
            .set('arvDrug', arvDrug ?? '')
            .set('start', start ?? 0)
            .set('pageSize', pageSize ?? 10);

        return this.http.get<PagedResult<StockRequest>>(this.resourceUrl, {params}).pipe(
            map(res => {
                res.data = res.data.map(request => {
                    request.date = DateTime.fromISO(request.date as unknown as string);
                    request.siteName = request.site.name;
                    return request;
                });
                return res;
            })
        );
    }

    ofcadSites() {
        return this.http.get<OrgUnit[]>('/api/impilo/ofcad-sites')
    }

    getAccount() {
        return this.http.get<any>('/api/account')
    }
}
