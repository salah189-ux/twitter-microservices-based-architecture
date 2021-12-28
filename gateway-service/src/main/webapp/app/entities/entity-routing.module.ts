import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'tweet',
        data: { pageTitle: 'Tweets' },
        loadChildren: () => import('./tweet/tweet/tweet.module').then(m => m.TweetTweetModule),
      },
      {
        path: 'follow',
        data: { pageTitle: 'Follows' },
        loadChildren: () => import('./socialgraph/follow/follow.module').then(m => m.SocialgraphFollowModule),
      },
      {
        path: 'message',
        data: { pageTitle: 'Messages' },
        loadChildren: () => import('./message/message/message.module').then(m => m.MessageMessageModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
