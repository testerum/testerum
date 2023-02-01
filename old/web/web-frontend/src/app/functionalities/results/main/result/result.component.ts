import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, NavigationEnd, Params, Router} from "@angular/router";
import {filter, map} from "rxjs/operators";
import {UrlUtil} from "../../../../utils/url.util";
import {Subscription} from "rxjs";

@Component({
    selector: 'result',
    templateUrl: 'result.component.html',
    styleUrls: ['result.component.scss']
})
export class ResultComponent {

    @Input() url: string;
}
