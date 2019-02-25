import {NgModule} from '@angular/core';
import {Routes, RouterModule} from "@angular/router";

const userRoutes: Routes = [
    {},
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
