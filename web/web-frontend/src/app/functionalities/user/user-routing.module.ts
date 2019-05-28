import {NgModule} from '@angular/core';
import {RouterModule, Routes} from "@angular/router";

const userRoutes: Routes = [
];
@NgModule({
    imports: [
        RouterModule.forChild(userRoutes)
    ],
    exports: [RouterModule],
    providers: [],
})
export class UserRoutingModule { 
}
