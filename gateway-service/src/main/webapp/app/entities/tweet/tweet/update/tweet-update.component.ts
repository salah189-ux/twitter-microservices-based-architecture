import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ITweet, Tweet } from '../tweet.model';
import { TweetService } from '../service/tweet.service';

@Component({
  selector: 'jhi-tweet-update',
  templateUrl: './tweet-update.component.html',
})
export class TweetUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    content: [null, [Validators.required]],
  });

  constructor(protected tweetService: TweetService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ tweet }) => {
      this.updateForm(tweet);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const tweet = this.createFromForm();
    if (tweet.id !== undefined) {
      this.subscribeToSaveResponse(this.tweetService.update(tweet));
    } else {
      this.subscribeToSaveResponse(this.tweetService.create(tweet));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITweet>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(tweet: ITweet): void {
    this.editForm.patchValue({
      id: tweet.id,
      content: tweet.content,
    });
  }

  protected createFromForm(): ITweet {
    return {
      ...new Tweet(),
      id: this.editForm.get(['id'])!.value,
      content: this.editForm.get(['content'])!.value,
    };
  }
}
