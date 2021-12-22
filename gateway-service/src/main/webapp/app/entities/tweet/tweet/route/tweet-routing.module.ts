import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TweetComponent } from '../list/tweet.component';
import { TweetDetailComponent } from '../detail/tweet-detail.component';
import { TweetUpdateComponent } from '../update/tweet-update.component';
import { TweetRoutingResolveService } from './tweet-routing-resolve.service';

const tweetRoute: Routes = [
  {
    path: '',
    component: TweetComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TweetDetailComponent,
    resolve: {
      tweet: TweetRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TweetUpdateComponent,
    resolve: {
      tweet: TweetRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TweetUpdateComponent,
    resolve: {
      tweet: TweetRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(tweetRoute)],
  exports: [RouterModule],
})
export class TweetRoutingModule {}
