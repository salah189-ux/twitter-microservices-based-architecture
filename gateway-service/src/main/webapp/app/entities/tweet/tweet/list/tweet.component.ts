import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ITweet } from '../tweet.model';
import { TweetService } from '../service/tweet.service';
import { TweetDeleteDialogComponent } from '../delete/tweet-delete-dialog.component';

@Component({
  selector: 'jhi-tweet',
  templateUrl: './tweet.component.html',
})
export class TweetComponent implements OnInit {
  tweets?: ITweet[];
  isLoading = false;

  constructor(protected tweetService: TweetService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.tweetService.query().subscribe(
      (res: HttpResponse<ITweet[]>) => {
        this.isLoading = false;
        this.tweets = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: ITweet): number {
    return item.id!;
  }

  delete(tweet: ITweet): void {
    const modalRef = this.modalService.open(TweetDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.tweet = tweet;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
