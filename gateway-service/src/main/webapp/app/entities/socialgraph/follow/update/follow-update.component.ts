import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IFollow, Follow } from '../follow.model';
import { FollowService } from '../service/follow.service';
import { Following } from 'app/entities/enumerations/following.model';

@Component({
  selector: 'jhi-follow-update',
  templateUrl: './follow-update.component.html',
})
export class FollowUpdateComponent implements OnInit {
  isSaving = false;
  followingValues = Object.keys(Following);

  editForm = this.fb.group({
    id: [],
    follow: [],
  });

  constructor(protected followService: FollowService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ follow }) => {
      this.updateForm(follow);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const follow = this.createFromForm();
    if (follow.id !== undefined) {
      this.subscribeToSaveResponse(this.followService.update(follow));
    } else {
      this.subscribeToSaveResponse(this.followService.create(follow));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IFollow>>): void {
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

  protected updateForm(follow: IFollow): void {
    this.editForm.patchValue({
      id: follow.id,
      follow: follow.follow,
    });
  }

  protected createFromForm(): IFollow {
    return {
      ...new Follow(),
      id: this.editForm.get(['id'])!.value,
      follow: this.editForm.get(['follow'])!.value,
    };
  }
}
