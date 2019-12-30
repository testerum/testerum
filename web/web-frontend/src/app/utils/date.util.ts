import {StringUtils} from "./string-utils.util";
import {ObjectUtil} from "./object.util";

export class DateUtil {

    static dateToShortString(date: Date): string {
        let year = date.getFullYear(),
            month = date.getMonth() + 1, // months are zero indexed
            day = date.getDate();

        return DateUtil.twoDigitToString(day)
            + "-"
            + DateUtil.twoDigitToString(month)
            + "-"
            + DateUtil.twoDigitToString(year);
    }

    static dateTimeToShortString(date: Date): string {
        let year = date.getFullYear(),
            month = date.getMonth() + 1, // months are zero indexed
            day = date.getDate(),
            hour = date.getHours(),
            minute = date.getMinutes();

        return DateUtil.twoDigitToString(day)
            + "-"
            + DateUtil.twoDigitToString(month)
            + "-"
            + DateUtil.twoDigitToString(year)
            + " "
            + DateUtil.twoDigitToString(hour)
            + ":"
            + DateUtil.twoDigitToString(minute);
    }

    // returns the date in the following format:
    // yyyy-MM-ddTHH:mm:ss.SSSZ
    static dateToIsoDateString(date: Date): string {
        if(!date) return null;

        let year = date.getFullYear(),
            month = date.getMonth() + 1, // months are zero indexed
            day = date.getDate(),
            hour = date.getHours(),
            minute = date.getMinutes(),
            seconds = date.getSeconds(),
            millis = date.getMilliseconds();


        return year
            + "-"
            + DateUtil.twoDigitToString(month)
            + "-"
            + DateUtil.twoDigitToString(day)
            + "T"
            + DateUtil.twoDigitToString(hour)
            + ":"
            + DateUtil.twoDigitToString(minute)
            + ":"
            + DateUtil.twoDigitToString(seconds)
            + "."
            + DateUtil.threeDigitToString(millis)
            + "Z";
    }

    private static isoDateRegexp = new RegExp('(\\d{4}-[01]\\d-[0-3]\\d(T[0-2]\\d:[0-5]\\d:[0-5]\\d(\\.\\d+([+-][0-2]\\d:[0-5]\\d|Z))?)?)');
    static isoDateStringToDate(serverDateAsString: string): Date | null {
        if (serverDateAsString == null || !this.isoDateRegexp.test(serverDateAsString)) { return null; }

        var b = serverDateAsString.split(/\D+/);
        let year    = ObjectUtil.getAsNumber(b[0]);
        let month   = ObjectUtil.getAsNumber(b[1]) - 1;
        let day     = ObjectUtil.getAsNumber(b[2]);
        let hours   = (b.length >= 3 && ObjectUtil.isANumber(b[3])) ? ObjectUtil.getAsNumber(b[3]) : 0;
        let minutes = (b.length >= 4 && ObjectUtil.isANumber(b[4])) ? ObjectUtil.getAsNumber(b[4]) : 0;
        let seconds = (b.length >= 5 && ObjectUtil.isANumber(b[5])) ? ObjectUtil.getAsNumber(b[5]) : 0;
        let millis  = (b.length >= 6 && ObjectUtil.isANumber(b[6])) ? ObjectUtil.getAsNumber(b[6]) : 0;

        return new Date(
            year,
            month,
            day,
            hours,
            minutes,
            seconds,
            millis);
    }

    private static twoDigitToString(number: number): string {
        return number < 10 ? "0"+number : ""+number;
    }

    private static threeDigitToString(number: number): string {
        if(number < 10) return "00"+number;
        if(number < 100) return "0"+number;
        return ""+number;
    }

    static durationToShortString(durantionInMillis: number): string {
        let date = new Date(durantionInMillis),
            hours = Math.floor(durantionInMillis / (3600*1000)),
            minutes = date.getMinutes(),
            seconds = date.getSeconds(),
            millis = ("" + date.getUTCMilliseconds()).slice(0, 2);

        let result = "" + millis;
        let suffix = "millis";

        if (seconds) {
            result = "" + seconds + "." + result;
            suffix = "seconds";
        }

        if (minutes) {
            result = "" + minutes + ":" + result;
            suffix = "minutes";
        }
        if (hours) {
            result = "" + hours + ":" + result;
            suffix = "hours";
        }

        return result + " " + suffix;
    }

    static getDaysBetweenDates(startDate: Date, endDate: Date): number {
        let millisInADay = 24*60*60*1000; // hours*minutes*seconds*milliseconds
        return Math.round(Math.abs((endDate.getTime() - startDate.getTime())/millisInADay));
    }

    static getHoursBetweenDates(startDate: Date, endDate: Date): number {
        let millisInAHour = 60*60*1000; // minutes*seconds*milliseconds
        return Math.round(Math.abs((endDate.getTime() - startDate.getTime())/millisInAHour));
    }

    static getMinutesBetweenDates(startDate: Date, endDate: Date): number {
        let millisInAMinute = 60*1000; // seconds*milliseconds
        return Math.round(Math.abs((endDate.getTime() - startDate.getTime())/millisInAMinute));
    }

    static getSecondsBetweenDates(startDate: Date, endDate: Date): number {
        let millisInASecond = 60*1000; // milliseconds
        return Math.round(Math.abs((endDate.getTime() - startDate.getTime())/millisInASecond));
    }

    private static regexp = new RegExp('(\\d{1,2})-(\\d{1,2})-(\\d{4})(\\s+(\\d{1,2}):(\\d{1,2})(:(\\d{1,2}))?)?');
    static stringToDate(dateAsString: string): Date| null {

        if(StringUtils.isEmpty(dateAsString)) {return null;}

        if (!this.regexp.test(dateAsString)) { return null; }

        let dateParts = dateAsString.split(" ");
        let datePartAsString = dateParts.length > 0 ? dateParts[0] : null;
        let timePartAsString = dateParts.length > 1 ? dateParts[1] : null;

        if(datePartAsString == null) return null;

        dateParts = datePartAsString.split('-');
        let day   = dateParts.length > 0 && ObjectUtil.isANumber(dateParts[0]) ?  ObjectUtil.getAsNumber(dateParts[0]) : null;
        let month = dateParts.length > 1 && ObjectUtil.isANumber(dateParts[1]) ?  ObjectUtil.getAsNumber(dateParts[1]) - 1 : null;
        let year  = dateParts.length > 2 && ObjectUtil.isANumber(dateParts[2]) ?  ObjectUtil.getAsNumber(dateParts[2]) : null;

        if (timePartAsString) {
            let hour   = timePartAsString.length > 0 && ObjectUtil.isANumber(timePartAsString[0]) ?  ObjectUtil.getAsNumber(timePartAsString[0]) : null;
            let minute = timePartAsString.length > 1 && ObjectUtil.isANumber(timePartAsString[1]) ?  ObjectUtil.getAsNumber(timePartAsString[1]) : null;
            let second = timePartAsString.length > 2 && ObjectUtil.isANumber(timePartAsString[2]) ?  ObjectUtil.getAsNumber(timePartAsString[2]) : null;

            return new Date(year, month, day, hour, minute, second)
        }

        return new Date(year, month, day)
    }
}
