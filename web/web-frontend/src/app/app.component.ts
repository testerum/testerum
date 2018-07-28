import { Component } from '@angular/core';
import {MessageService} from "./service/message.service";

@Component({
  moduleId:module.id,
  selector: 'my-app',
  templateUrl: './app.component.html'
})
export class AppComponent  {

    constructor(messageService: MessageService) {
        messageService.init()
    }
}
