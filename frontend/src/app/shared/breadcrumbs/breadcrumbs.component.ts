import { Component, OnDestroy } from '@angular/core';
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
      (data) => {
        if (data) {
          this.section = data['section'] ?? '';
          this.title = data['title'] ?? '';
          document.title = this.title ? `HdH - ${this.title}` : 'HdH';
        }
      }
    );
  }

  ngOnDestroy(): void {
    this.sectionTitleSubs$?.unsubscribe();
  }

  getDataRoute() {
    return this.router.events.pipe(
      filter((event: any) => event instanceof ActivationEnd && event.snapshot.firstChild === null),
      map((event: ActivationEnd) => event.snapshot.data)
    );
  }
}
