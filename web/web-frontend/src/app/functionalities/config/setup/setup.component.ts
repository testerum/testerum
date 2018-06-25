import {Component, OnInit} from '@angular/core';
import {Setup} from "./model/setup.model";
import {SetupService} from "../../../service/setup.service";
import {Path} from "../../../model/infrastructure/path/path.model";
import {Router} from "@angular/router";

@Component({
    selector: 'setup',
    templateUrl: 'setup.component.html',
    styleUrls: ['setup.component.css', '../../../generic/css/generic.css', '../../../generic/css/forms.css']
})

export class SetupComponent implements OnInit {

    repositoryPathAsString: string;

    constructor(private router: Router,
                private startConfigService: SetupService) {
    }

    ngOnInit() {
        this.startConfigService.isConfigSet().subscribe(
            (isConfigSet: boolean) => {
                if(isConfigSet) {
                    this.router.navigate(['']);
                }
            }
        );
    }

    save(): void {
        let startConfig = new Setup();
        startConfig.repositoryPath = Path.createInstance(this.repositoryPathAsString);

        this.startConfigService.save(startConfig).subscribe(
            result => {
                this.router.navigate(['']);
            }
        )
    }
}
