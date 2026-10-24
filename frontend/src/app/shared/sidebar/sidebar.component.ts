import { Component, OnInit } from '@angular/core';
import { SidebarService } from '../../services/sidebar.service';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: [],
})
export class SidebarComponent implements OnInit {
  menus: any[];

  constructor(private sidebarService: SidebarService) {
    this.menus = sidebarService.menus;
  }

  ngOnInit(): void {}
}
