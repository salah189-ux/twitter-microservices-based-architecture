jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { TweetService } from '../service/tweet.service';
import { ITweet, Tweet } from '../tweet.model';

import { TweetUpdateComponent } from './tweet-update.component';

describe('Tweet Management Update Component', () => {
  let comp: TweetUpdateComponent;
  let fixture: ComponentFixture<TweetUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let tweetService: TweetService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [TweetUpdateComponent],
      providers: [FormBuilder, ActivatedRoute],
    })
      .overrideTemplate(TweetUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TweetUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    tweetService = TestBed.inject(TweetService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const tweet: ITweet = { id: 456 };

      activatedRoute.data = of({ tweet });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(tweet));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Tweet>>();
      const tweet = { id: 123 };
      jest.spyOn(tweetService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tweet });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: tweet }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(tweetService.update).toHaveBeenCalledWith(tweet);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Tweet>>();
      const tweet = new Tweet();
      jest.spyOn(tweetService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tweet });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: tweet }));
      saveSubject.complete();

      // THEN
      expect(tweetService.create).toHaveBeenCalledWith(tweet);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Tweet>>();
      const tweet = { id: 123 };
      jest.spyOn(tweetService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tweet });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(tweetService.update).toHaveBeenCalledWith(tweet);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
