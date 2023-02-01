import {Injectable} from '@angular/core';
import {ReportInfo} from "../model/report-info.model";
import {Observable} from "rxjs";
import {HttpClient} from "@angular/common/http";

@Injectable()
export class ReportService {

  private REPORTS_URL = "v1/reports";

  constructor(private http: HttpClient) {
  }

  getReportsInfo(): Observable<Array<ReportInfo>>  {
    return this.http.get<Array<ReportInfo>>(this.REPORTS_URL)
  }
}
