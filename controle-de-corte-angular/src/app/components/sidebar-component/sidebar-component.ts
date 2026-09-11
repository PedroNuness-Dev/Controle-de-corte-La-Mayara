import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { RouterLinkActive, RouterLink, RouterModule, ActivatedRoute } from "@angular/router";
import { CorteResponse } from '../../interfaces/corte/CorteResponse';


@Component({
  selector: 'app-sidebar-component',
  imports: [RouterLinkActive, RouterLink, RouterModule],
  templateUrl: './sidebar-component.html',
  styleUrl: './sidebar-component.scss',
})
export class SidebarComponent implements OnInit {

  route = inject(ActivatedRoute);
  cdr = inject(ChangeDetectorRef);
  pageSelected : string | null = null;
  cortes !: CorteResponse[];

  ngOnInit(){
    this.route.params.subscribe(params => {
      this.pageSelected = params['tipo'];
      if(this.pageSelected == null) this.pageSelected = 'Geral';
    })
  }
}
