import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivationEnd, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { filter, map } from 'rxjs/operators';

@Component({
  selector: 'app-breadcrumbs',
  templateUrl: './breadcrumbs.component.html',
  styleUrls: [],
})
export class BreadcrumbsComponent implements OnDestroy {
  public section: string = '';
  public title: string = '';
  public sectionTitleSubs$: Subscription | undefined;

  constructor(private router: Router) {
    this.sectionTitleSubs$ = this.getDataRoute().subscribe(
      ({ section, title }) => {
        this.section = section;
        this.title = title;
        document.title = `HdH - ${title}`;
      }
    );
  }

  ngOnDestroy(): void {
    this.sectionTitleSubs$?.unsubscribe();
  }

  getDataRoute() {
    return this.router.events.pipe(
      filter((event: any) => event instanceof ActivationEnd),
      map((event: ActivationEnd) => event.snapshot.data)
    );
  }
}
