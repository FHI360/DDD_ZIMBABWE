import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import { DateTime } from 'luxon';
import { map } from 'rxjs';
import {PagedResult} from "@mattae/angular-shared";
import {OrgUnit} from "../stock/stock.service";

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

export interface Refill {
    date: DateTime,
    dateNextRefill: DateTime,
    qtyDispensed?: number;
    qtyPrescribed?: number;
    regimen?: string;
}

@Injectable()
export class PatientService {
    private resourceUrl = '/api/impilo/patients';

    constructor(private http: HttpClient) {
    }

    list(keyword?: string, assigned?: boolean, start?: number, pageSize?: number) {
        let params = new HttpParams()
            .set('keyword', keyword ?? '')
            .set('start', (start ?? 0).toString())
            .set('pageSize', (pageSize ?? 10).toString());
        if (assigned) {
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

    devolvePatient(organisationId: any, patientId: any) {
        return this.http.get<boolean>(`${this.resourceUrl}/devolve/patient/${patientId}/organisation/${organisationId}`)
    }

    unDevolvePatient(patientId: any) {
        return this.http.get<boolean>(`${this.resourceUrl}/unassign/patient/${patientId}`)
    }

    ofcadSites() {
        return this.http.get<OrgUnit[]>('/api/impilo/ofcad-sites')
    }

    getPatientById(id: any) {
        return this.http.get<Patient>(`${this.resourceUrl}/${id}`).pipe(
            map(res => {
                return Object.assign({}, res, {
                    dateOfBirth: res.dateOfBirth != null ? DateTime.fromISO(res.dateOfBirth as unknown as string) : null,
                    lastRefillDate: res.lastRefillDate != null ? DateTime.fromISO(res.lastRefillDate as unknown as string) : null,
                    nextRefillDate: res.nextRefillDate != null ? DateTime.fromISO(res.nextRefillDate as unknown as string) : null,
                    sex: res.sex.toLowerCase() === 'male' ? 'Male' : 'Female'
                })
            })
        )
    }

    getRefillsByPatient(patientId: any) {
        return this.http.get<Refill[]>(`${this.resourceUrl}/refills/patient/${patientId}`).pipe(
            map(res => {
                return res.map((r) => {
                    return Object.assign({}, r, {
                        date: r.date != null ? DateTime.fromISO(r.date as unknown as string) : null,
                        dateNextRefill: r.dateNextRefill != null ? DateTime.fromISO(r.dateNextRefill as unknown as string) : null
                    })
                })
            })
        )
    }
}
