import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITweet } from '../tweet.model';
import { TweetService } from '../service/tweet.service';

@Component({
  templateUrl: './tweet-delete-dialog.component.html',
})
export class TweetDeleteDialogComponent {
  tweet?: ITweet;

  constructor(protected tweetService: TweetService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.tweetService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
