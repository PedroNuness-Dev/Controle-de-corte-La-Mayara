import { Routes } from '@angular/router';
import { ContentComponent } from './components/content-component/content-component';
import { HistoricoComponent } from './components/historico-component/historico-component';
import { ConfigComponent } from './components/config-component/config-component';
import { HomeComponent } from './components/home-component/home-component';
import { SuporteComponent } from './components/suporte-component/suporte-component';

export const routes: Routes = [{
    path: "",
    component: HomeComponent
},{
    path:"cortes/:tipo",
    component: ContentComponent
},
{
    path:"historico",
    component: HistoricoComponent
},{
    path: "configuracoes",
    component: ConfigComponent
},
{
    path: "suporte",
    component: SuporteComponent
}]
