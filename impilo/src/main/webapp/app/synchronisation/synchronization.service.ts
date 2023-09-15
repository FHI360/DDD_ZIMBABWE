import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';

@Injectable()
export class SynchronizationService {
    readonly resourceUrl = '/api/impilo/server-sync';

    constructor(private http: HttpClient) {
    }

    syncEHR() {
        return this.http.get(`${this.resourceUrl}/ehr`);
    }

    syncCentralServer() {
        return this.http.get(`${this.resourceUrl}/central-server`);
    }

    scheduleSync(interval: number) {
        return this.http.post(`${this.resourceUrl}/schedule-sync`, interval);
    }

    syncInterval() {
        return this.http.get(`${this.resourceUrl}/sync-interval`)
    }
}
