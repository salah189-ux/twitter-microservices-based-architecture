import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IFollow } from '../follow.model';
import { FollowService } from '../service/follow.service';
import { FollowDeleteDialogComponent } from '../delete/follow-delete-dialog.component';

@Component({
  selector: 'jhi-follow',
  templateUrl: './follow.component.html',
})
export class FollowComponent implements OnInit {
  follows?: IFollow[];
  isLoading = false;

  constructor(protected followService: FollowService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.followService.query().subscribe(
      (res: HttpResponse<IFollow[]>) => {
        this.isLoading = false;
        this.follows = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IFollow): number {
    return item.id!;
  }

  delete(follow: IFollow): void {
    const modalRef = this.modalService.open(FollowDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.follow = follow;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
