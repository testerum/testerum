import {AreYouSureModalEnum} from "./are-you-sure-modal.enum";

export interface AreYouSureModalListener {
    (action:AreYouSureModalEnum): void;
}
