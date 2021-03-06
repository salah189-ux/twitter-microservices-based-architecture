import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { Following } from 'app/entities/enumerations/following.model';
import { IFollow, Follow } from '../follow.model';

import { FollowService } from './follow.service';

describe('Follow Service', () => {
  let service: FollowService;
  let httpMock: HttpTestingController;
  let elemDefault: IFollow;
  let expectedResult: IFollow | IFollow[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(FollowService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      follow: Following.FOLLOW,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Follow', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Follow()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Follow', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          follow: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Follow', () => {
      const patchObject = Object.assign({}, new Follow());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Follow', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          follow: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Follow', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addFollowToCollectionIfMissing', () => {
      it('should add a Follow to an empty array', () => {
        const follow: IFollow = { id: 123 };
        expectedResult = service.addFollowToCollectionIfMissing([], follow);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(follow);
      });

      it('should not add a Follow to an array that contains it', () => {
        const follow: IFollow = { id: 123 };
        const followCollection: IFollow[] = [
          {
            ...follow,
          },
          { id: 456 },
        ];
        expectedResult = service.addFollowToCollectionIfMissing(followCollection, follow);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Follow to an array that doesn't contain it", () => {
        const follow: IFollow = { id: 123 };
        const followCollection: IFollow[] = [{ id: 456 }];
        expectedResult = service.addFollowToCollectionIfMissing(followCollection, follow);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(follow);
      });

      it('should add only unique Follow to an array', () => {
        const followArray: IFollow[] = [{ id: 123 }, { id: 456 }, { id: 97859 }];
        const followCollection: IFollow[] = [{ id: 123 }];
        expectedResult = service.addFollowToCollectionIfMissing(followCollection, ...followArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const follow: IFollow = { id: 123 };
        const follow2: IFollow = { id: 456 };
        expectedResult = service.addFollowToCollectionIfMissing([], follow, follow2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(follow);
        expect(expectedResult).toContain(follow2);
      });

      it('should accept null and undefined values', () => {
        const follow: IFollow = { id: 123 };
        expectedResult = service.addFollowToCollectionIfMissing([], null, follow, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(follow);
      });

      it('should return initial array if no Follow is added', () => {
        const followCollection: IFollow[] = [{ id: 123 }];
        expectedResult = service.addFollowToCollectionIfMissing(followCollection, undefined, null);
        expect(expectedResult).toEqual(followCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
