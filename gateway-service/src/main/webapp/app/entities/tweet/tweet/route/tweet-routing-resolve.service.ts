import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITweet, Tweet } from '../tweet.model';
import { TweetService } from '../service/tweet.service';

@Injectable({ providedIn: 'root' })
export class TweetRoutingResolveService implements Resolve<ITweet> {
  constructor(protected service: TweetService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITweet> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((tweet: HttpResponse<Tweet>) => {
          if (tweet.body) {
            return of(tweet.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Tweet());
  }
}
