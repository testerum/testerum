import {Serializable} from "../infrastructure/serializable.model";

export class FormValidationModel implements Serializable<FormValidationModel> {

    globalMessage: string;
    globalHtmlMessage: string;
    globalMessageDetails: string;
    fieldsWithValidationErrors: Map<string, string> = new Map<string, string>();

    deserialize(input: Object): FormValidationModel {

        this.globalMessage = input["globalMessage"];
        this.globalHtmlMessage = input["globalHtmlMessage"];
        this.globalMessageDetails = input["globalMessageDetails"];
        let inputFieldsWithValidationErrors = input["fieldsWithValidationErrors"];
        Object.keys(inputFieldsWithValidationErrors).forEach( key => {
            let value = inputFieldsWithValidationErrors[key];
            this.fieldsWithValidationErrors.set(key, value);
        });

        return this;
    }

    serialize(): string {
        return null;
    }
}
