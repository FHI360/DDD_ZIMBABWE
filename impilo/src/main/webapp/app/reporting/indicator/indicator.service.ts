import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { DateTime } from 'luxon';

@Injectable()
export class IndicatorService {
    constructor(private http: HttpClient) {
    }

    generate(param: { start: DateTime, end: DateTime }) {
        const params = new HttpParams()
            .set('startDate', param.start.toISODate())
            .set('endDate', param.end.toISODate());
        return this.http.get(`/api/impilo/reporting/indicators`, {params, responseType: 'blob'})
    }
}
