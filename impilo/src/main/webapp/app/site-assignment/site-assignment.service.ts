import { DateTime } from 'luxon';
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { PagedResult } from '@mattae/angular-shared';
import { map } from 'rxjs';
import { OrgUnit } from '../stock/stock.service';

export interface Patient {
    id?: any;
    givenName?: string;
    familyName?: string;
    dateOfBirth?: DateTime
    sex?: string;
    phoneNumber?: string;
    address?: string;
    hospitalNumber?: string;
    facilityName?: string;
    regimen?: string;
    site?: string,
    facility?: string
}

@Injectable()
export class SiteAssignmentService {
    private resourceUrl = '/api/site-assignments';

    constructor(private http: HttpClient) {
    }

    list(keyword?: string, assigned?: boolean, start?: number, pageSize?: number) {
        let params = new HttpParams()
            .set('keyword', keyword ?? '')
            .set('start', (start ?? 0).toString())
            .set('pageSize', (pageSize ?? 10).toString());
        if (assigned !== undefined) {
            params = params.set('assigned', assigned);
        }
        return this.http.get<PagedResult<Patient>>(this.resourceUrl, {params}).pipe(
            map(patients => {
                patients.data = patients.data.map(patient => {
                    patient.dateOfBirth = DateTime.fromISO(patient.dateOfBirth as unknown as string);
                    return patient;
                });
                return patients;
            })
        )
    }

    assignPatient(siteId: any, patientId: any) {
        return this.http.get<boolean>(`${this.resourceUrl}/assign/patient/${patientId}/site/${siteId}`)
    }

    unassignPatient(patientId: any) {
        return this.http.get<boolean>(`${this.resourceUrl}/unassign/patient/${patientId}`)
    }

    ofcadSites() {
        return this.http.get<OrgUnit[]>('/api/impilo/ofcad-sites')
    }
}
