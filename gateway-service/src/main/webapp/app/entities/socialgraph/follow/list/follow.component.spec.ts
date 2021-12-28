import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { FollowService } from '../service/follow.service';

import { FollowComponent } from './follow.component';

describe('Follow Management Component', () => {
  let comp: FollowComponent;
  let fixture: ComponentFixture<FollowComponent>;
  let service: FollowService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [FollowComponent],
    })
      .overrideTemplate(FollowComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(FollowComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(FollowService);

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
    expect(comp.follows?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
