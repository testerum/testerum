import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {APP_BASE_HREF} from "@angular/common";
import {StatsComponent} from "./stats/stats.component";

const routes: Routes = [
    { path: '', component: StatsComponent },
    { path: 'stats', component: StatsComponent },
];

@NgModule({
    imports: [RouterModule.forRoot(routes, { useHash: true, relativeLinkResolution: 'legacy' })],
    exports: [RouterModule],
    providers: [
        {
            provide: APP_BASE_HREF,
            useValue: "/"
        }
    ]

})
export class AppRoutingModule { }
