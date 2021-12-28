jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { FollowService } from '../service/follow.service';
import { IFollow, Follow } from '../follow.model';

import { FollowUpdateComponent } from './follow-update.component';

describe('Follow Management Update Component', () => {
  let comp: FollowUpdateComponent;
  let fixture: ComponentFixture<FollowUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let followService: FollowService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [FollowUpdateComponent],
      providers: [FormBuilder, ActivatedRoute],
    })
      .overrideTemplate(FollowUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(FollowUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    followService = TestBed.inject(FollowService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const follow: IFollow = { id: 456 };

      activatedRoute.data = of({ follow });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(follow));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Follow>>();
      const follow = { id: 123 };
      jest.spyOn(followService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ follow });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: follow }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(followService.update).toHaveBeenCalledWith(follow);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Follow>>();
      const follow = new Follow();
      jest.spyOn(followService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ follow });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: follow }));
      saveSubject.complete();

      // THEN
      expect(followService.create).toHaveBeenCalledWith(follow);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Follow>>();
      const follow = { id: 123 };
      jest.spyOn(followService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ follow });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(followService.update).toHaveBeenCalledWith(follow);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
