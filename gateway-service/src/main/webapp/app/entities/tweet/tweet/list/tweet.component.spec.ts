import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { TweetService } from '../service/tweet.service';

import { TweetComponent } from './tweet.component';

describe('Tweet Management Component', () => {
  let comp: TweetComponent;
  let fixture: ComponentFixture<TweetComponent>;
  let service: TweetService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [TweetComponent],
    })
      .overrideTemplate(TweetComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TweetComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(TweetService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.tweets?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
