import {map} from 'rxjs/operators';
import {Injectable} from "@angular/core";
import {Message} from "../model/messages/message.model";
import {MessageKey} from "../model/messages/message.enum";
import {HttpClient} from "@angular/common/http";
import {Location} from "@angular/common";

@Injectable()
export class MessageService {

    private messages: Object;

    private readonly baseUrl: string;

    constructor(private http: HttpClient,
                location: Location) {
        this.baseUrl = location.prepareExternalUrl("/rest/messages");
    }

    init() {
        return new Promise((resolve, reject) => {
            this.http
                .get<Array<Message>>(this.baseUrl).pipe(
                map(this.extractMessages))
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
