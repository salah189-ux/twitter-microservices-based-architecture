import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { TweetDetailComponent } from './tweet-detail.component';

describe('Tweet Management Detail Component', () => {
  let comp: TweetDetailComponent;
  let fixture: ComponentFixture<TweetDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TweetDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ tweet: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(TweetDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(TweetDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load tweet on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.tweet).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
