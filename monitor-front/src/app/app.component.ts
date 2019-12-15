import {Component} from '@angular/core';
import {Event, NavigationCancel, NavigationEnd, NavigationError, NavigationStart, Router} from '@angular/router';
import {NzMessageService} from 'ng-zorro-antd';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  loaderMessageId;

  constructor(private router: Router, private messageService: NzMessageService) {

    this.router.events.subscribe((event: Event) => {

      switch (true) {
        case event instanceof NavigationStart: {
          this.loaderMessageId = this.messageService.loading('Loading ...', { nzDuration: 0 }).messageId;
          break;
        }

        case event instanceof NavigationEnd:
        case event instanceof NavigationCancel:
        case event instanceof NavigationError: {
          this.messageService.remove(this.loaderMessageId);
          break;
        }

        default: {
          break;
        }
      }

    });

  }
}
