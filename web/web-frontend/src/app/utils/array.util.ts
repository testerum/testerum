
import {JsonUtil} from "./json.util";
import {HeadersList} from "../functionalities/resources/editors/http/request/header/model/headers-list.model";

export class ArrayUtil {

    static copyArray(array: Array<any>): Array<any> {
        return JSON.parse(JsonUtil.serializeArray(array));
    }


    static mapArray<T>(source: Array<any>):T[] {
        let result: T[] = [];
        for (const obj of source) {
            result.push(obj)
        }
        return result;
    }

    static copyArrayOfObjects(array: Array<any>): Array<any> {
        let result: Array<any> = [];
        for (const obj of array) {
            result.push(obj)
        }
        return result;
    }

    static removeElementFromArray(array: Array<any>, element: any): boolean {
        let index = array.indexOf(element, 0);
        if (index > -1) {
            array.splice(index, 1);
            return true;
        }
        return false;
    }

    static replaceElementInArray(array: Array<any>, oldElement: any, newElement: any) {
        let index = ``
        if (index > -1) {
            array[index] = newElement;
            return true;
        }
        return false;
    }

    static containsElement(array: Array<string>, element: string): boolean {
        return !this.doesNotContainElement(array, element);
    }

    static doesNotContainElement(array: Array<string>, element: string): boolean {
        return array.indexOf(element) < 0
    }

    static sort(array: Array<string>) {
        array.sort((a,b) => a > b ? 1 : -1);
    }

    static replaceElementsInArray(mainArray: Array<any>, newElements: Array<any>) {
        mainArray.length=0;
        for (let newElement of newElements) {
            mainArray.push(newElement)
        }
    }

    static filterArray(array: Array<string>, filterKeyWord: string): Array<string> {

        let result = [];
        for (let word of array) {
            if(word.toLowerCase().indexOf(filterKeyWord.toLowerCase()) == 0) {
                result.push(word);
            }
        }
        return result;
    }

    static copyArrayOfImmutableObjects(items: Array<any>): Array<any> {
        let result = [];
        for (const item of items) {
            result.push(item);
        }
        return result;
    }
}
