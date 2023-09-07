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
    lastRefillDate?: DateTime;
    nextRefillDate?: DateTime;
    sex?: string;
    phoneNumber?: string;
    address?: string;
    hospitalNumber?: string;
    uniqueId?: string;
    assignedRegimen?: string;
    site?: string,
    facility?: string
}

@Injectable()
export class DevolveService {
    private resourceUrl = '/api/impilo/devolves';

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
                    patient.sex = patient.sex.toLowerCase() === 'male' ? 'Male' : 'Female';
                    return patient;
                });
                return patients;
            })
        )
    }

    assignPatient(organisationId: any, patientId: any) {
        return this.http.get<boolean>(`${this.resourceUrl}/devolve/patient/${patientId}/organisation/${organisationId}`)
    }

    unassignPatient(patientId: any) {
        return this.http.get<boolean>(`${this.resourceUrl}/unassign/patient/${patientId}`)
    }

    ofcadSites() {
        return this.http.get<OrgUnit[]>('/api/impilo/ofcad-sites')
    }
}
