import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { TweetComponent } from './list/tweet.component';
import { TweetDetailComponent } from './detail/tweet-detail.component';
import { TweetUpdateComponent } from './update/tweet-update.component';
import { TweetDeleteDialogComponent } from './delete/tweet-delete-dialog.component';
import { TweetRoutingModule } from './route/tweet-routing.module';

@NgModule({
  imports: [SharedModule, TweetRoutingModule],
  declarations: [TweetComponent, TweetDetailComponent, TweetUpdateComponent, TweetDeleteDialogComponent],
  entryComponents: [TweetDeleteDialogComponent],
})
export class TweetTweetModule {}
