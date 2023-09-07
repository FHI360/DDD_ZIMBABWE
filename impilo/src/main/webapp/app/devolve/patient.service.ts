import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { DateTime } from 'luxon';
import { map } from 'rxjs';
import { Patient } from './devolve.service';

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

    getPatientById(id: any) {
        return this.http.get<Patient>(`${this.resourceUrl}/${id}`).pipe(
            map(res => {
                return Object.assign({}, res, {
                    dateOfBirth: res.dateOfBirth != null ? DateTime.fromISO(res.dateOfBirth as unknown as string) : null,
                    lastRefillDate: res.lastRefillDate != null ? DateTime.fromISO(res.lastRefillDate as unknown as string) : null,
                    nextRefillDate: res.nextRefillDate != null ? DateTime.fromISO(res.nextRefillDate as unknown as string) : null
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
