
export interface ComponentCanDeactivate {

    canDeactivate(): boolean;

    unloadPageNotification($event: any)
}
