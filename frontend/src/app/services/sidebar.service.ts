import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class SidebarService {
  menus: any[] = [
    {
      icon: 'mdi mdi-widgets',
      title: 'Tecas',
      submenu: [
        { title: 'Main', path: 'tecas' },
        { title: 'Books', path: 'tecas/books' },
        { title: 'Films', path: 'tecas/films' },
        { title: 'Series', path: 'tecas/series' },
      ],
    },
  ];

  constructor() {}
}
