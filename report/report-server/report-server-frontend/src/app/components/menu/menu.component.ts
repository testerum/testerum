import {Component} from '@angular/core';
import {NavigationEnd, Router} from "@angular/router";

@Component({
  selector: 'menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.scss']
})
export class MenuComponent {

  shouldDisplayReportsLink: boolean = true;

  constructor(private router: Router) {

    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        switch (event.url) {
          case "/reports": {
            this.shouldDisplayReportsLink = false;
            break;
          }
          default: {
            this.shouldDisplayReportsLink = true;
          }
        }
      }
    });
  }
}
