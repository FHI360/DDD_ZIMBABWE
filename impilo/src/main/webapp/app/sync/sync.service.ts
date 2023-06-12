import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
    providedIn: 'root'
})
export class SyncService {
    constructor(private http: HttpClient) {
    }

    sync(username: string, password: string) {
        return this.http.post<boolean>('api/impilo/sync', {username: username, password: password})
    }
}
