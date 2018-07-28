import {Injectable} from "@angular/core";
import {Message} from "../model/messages/message.model";
import {Setting} from "../functionalities/config/settings/model/setting.model";
import {MessageKey} from "../model/messages/message.enum";
import {HttpClient} from "@angular/common/http";


@Injectable()
export class MessageService {

    private messages: Object;

    private BASE_URL = "/rest/messages";

    constructor(private http: HttpClient) {}

    init() {
        return new Promise((resolve, reject) => {
            this.http
                .get<Array<Message>>(this.BASE_URL)
                .map(this.extractMessages)
                .subscribe( result => {
                    this.messages = result;
                    resolve(true);
                });

        });
    }

    private extractMessages(messages:  Array<Message>): Object{
        let response: Object = {};
        for (let message of messages) {
            response[message.key] = message.value;
        }

        return response;
    }

    getMessage(key: MessageKey): string {
        let keyEnum = MessageKey[key];
        return this.messages[keyEnum];
    }
}
