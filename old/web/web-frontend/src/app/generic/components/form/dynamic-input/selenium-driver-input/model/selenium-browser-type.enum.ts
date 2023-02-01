import {Enum} from "../../../../../../model/enums/enum.interface";

export class SeleniumBrowserType extends Enum {

    public static CHROME = new SeleniumBrowserType("CHROME", "Chrome", "fab fa-chrome");
    public static FIREFOX = new SeleniumBrowserType("FIREFOX", "Firefox", "fab fa-firefox");
    public static OPERA = new SeleniumBrowserType("OPERA", "Opera", "fab fa-opera");
    public static EDGE = new SeleniumBrowserType("EDGE", "Edge", "fab fa-edge");
    public static INTERNET_EXPLORER = new SeleniumBrowserType("INTERNET_EXPLORER", "Internet Explorer", "fab fa-internet-explorer");
    public static SAFARI = new SeleniumBrowserType("SAFARI", "Safari", "fab fa-safari");
    public static REMOTE = new SeleniumBrowserType("REMOTE", "Remote", "fas fa-cloud");

    static readonly enums: Array<SeleniumBrowserType> = [
        SeleniumBrowserType.CHROME,
        SeleniumBrowserType.FIREFOX,
        SeleniumBrowserType.OPERA,
        SeleniumBrowserType.EDGE,
        SeleniumBrowserType.INTERNET_EXPLORER,
        SeleniumBrowserType.SAFARI,
        SeleniumBrowserType.REMOTE,
    ];

    public readonly icon: string;
    public readonly asSerialized: string;
    private constructor(asSerialized: string, asString: string, icon: string) {
        super(asString);
        this.asSerialized = asSerialized;
        this.icon = icon;
    }

    public static fromString(browserTypeAsString: string): SeleniumBrowserType {
        for (let enumValue of SeleniumBrowserType.enums) {
            if(enumValue.enumAsString.toUpperCase() == browserTypeAsString.toUpperCase()) {
                return enumValue;
            }
        }

        throw new Error("Selenium browser type ["+browserTypeAsString+"] not configured")
    }

    public static fromSerialization(asSerialized: string): SeleniumBrowserType {
        if(!asSerialized) return null;

        for (let enumValue of SeleniumBrowserType.enums) {
            if(enumValue.asSerialized.toUpperCase() == asSerialized.toUpperCase()) {
                return enumValue;
            }
        }

        throw new Error("Selenium browser type ["+asSerialized+"] not configured")
    }
}
