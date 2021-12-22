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
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
