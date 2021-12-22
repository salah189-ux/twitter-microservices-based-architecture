import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ITweet, Tweet } from '../tweet.model';

import { TweetService } from './tweet.service';

describe('Tweet Service', () => {
  let service: TweetService;
  let httpMock: HttpTestingController;
  let elemDefault: ITweet;
  let expectedResult: ITweet | ITweet[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TweetService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      content: 'AAAAAAA',
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

    it('should create a Tweet', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Tweet()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Tweet', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          content: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Tweet', () => {
      const patchObject = Object.assign(
        {
          content: 'BBBBBB',
        },
        new Tweet()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Tweet', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          content: 'BBBBBB',
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

    it('should delete a Tweet', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addTweetToCollectionIfMissing', () => {
      it('should add a Tweet to an empty array', () => {
        const tweet: ITweet = { id: 123 };
        expectedResult = service.addTweetToCollectionIfMissing([], tweet);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(tweet);
      });

      it('should not add a Tweet to an array that contains it', () => {
        const tweet: ITweet = { id: 123 };
        const tweetCollection: ITweet[] = [
          {
            ...tweet,
          },
          { id: 456 },
        ];
        expectedResult = service.addTweetToCollectionIfMissing(tweetCollection, tweet);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Tweet to an array that doesn't contain it", () => {
        const tweet: ITweet = { id: 123 };
        const tweetCollection: ITweet[] = [{ id: 456 }];
        expectedResult = service.addTweetToCollectionIfMissing(tweetCollection, tweet);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(tweet);
      });

      it('should add only unique Tweet to an array', () => {
        const tweetArray: ITweet[] = [{ id: 123 }, { id: 456 }, { id: 54009 }];
        const tweetCollection: ITweet[] = [{ id: 123 }];
        expectedResult = service.addTweetToCollectionIfMissing(tweetCollection, ...tweetArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const tweet: ITweet = { id: 123 };
        const tweet2: ITweet = { id: 456 };
        expectedResult = service.addTweetToCollectionIfMissing([], tweet, tweet2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(tweet);
        expect(expectedResult).toContain(tweet2);
      });

      it('should accept null and undefined values', () => {
        const tweet: ITweet = { id: 123 };
        expectedResult = service.addTweetToCollectionIfMissing([], null, tweet, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(tweet);
      });

      it('should return initial array if no Tweet is added', () => {
        const tweetCollection: ITweet[] = [{ id: 123 }];
        expectedResult = service.addTweetToCollectionIfMissing(tweetCollection, undefined, null);
        expect(expectedResult).toEqual(tweetCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
