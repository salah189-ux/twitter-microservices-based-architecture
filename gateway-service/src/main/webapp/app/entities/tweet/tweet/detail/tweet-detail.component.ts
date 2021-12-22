import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITweet } from '../tweet.model';

@Component({
  selector: 'jhi-tweet-detail',
  templateUrl: './tweet-detail.component.html',
})
export class TweetDetailComponent implements OnInit {
  tweet: ITweet | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ tweet }) => {
      this.tweet = tweet;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
