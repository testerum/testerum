import {AfterViewInit, Component, ComponentRef, ViewChild} from '@angular/core';
import {ModalDirective} from "ngx-bootstrap";
import {ErrorHttpInterceptor} from "../../../service/interceptors/error.http-interceptor";
import {UtilService} from "../../../service/util.service";
import {interval, Subscription} from "rxjs";
import {map} from "rxjs/operators";

@Component({
  selector: 'server-not-available',
  templateUrl: './server-not-available-modal.component.html',
  styleUrls: ['./server-not-available-modal.component.scss']
})
export class ServerNotAvailableModalComponent implements AfterViewInit {

    @ViewChild("serverNotAvailableModal", { static: true }) modal:ModalDirective;

    modalComponentRef: ComponentRef<ServerNotAvailableModalComponent>;
    shouldRefreshWhenServerIsBack: boolean = false;

    RETRY_NUMBER_OF_SECONDS: number = 5;
    retryIn: number = this.RETRY_NUMBER_OF_SECONDS;

    counterSubscription: Subscription;

    constructor(private utilService: UtilService,) {
    }

    ngAfterViewInit(): void {
        this.modal.show();
        this.modal.onHidden.subscribe(event => {
            this.modalComponentRef.destroy();

            this.modalComponentRef = null;
        });

        this.counterSubscription = interval(1000).subscribe((x) => {
            this.retryIn -= 1;
            if (this.retryIn < 0) {
                this.retryIn = this.RETRY_NUMBER_OF_SECONDS;
                this.retry();
            }
        });
    }

    retry() {
        this.utilService.checkIfServerIsAvailable().subscribe((isServerAvailable: boolean) => {
            if(isServerAvailable) {
                this.counterSubscription.unsubscribe();
                ErrorHttpInterceptor.isServerAvailable = true;
                this.modal.hide();

                if (this.shouldRefreshWhenServerIsBack) {
                    window.location.reload();
                }
            }
        })
    }
}
