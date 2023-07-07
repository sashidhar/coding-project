## MVP

1. Set availability for a user
2. Show availability for a user
3. Show overlapping availability for two users for a given date.
4. Set recurring availability for a user
5. Deleting availability for a user (the deletion availability can partially overlap with one or more existing availabilities)


## Assumptions
1. The Dates/timestamps are assumed to be in UTC. Assumption is that UI/front-end layer does the conversion from other timezones to UTC.

## Design tradeoffs

1. Chose JPA persistence it's easy to switch to a different DB with no to minimal query changes.

2. In case of unique constraint violation on user or user availability, an exception is thrown to keep the design simple, instead of a silent merge on the backend which can make the design complex.

3. Date range for availability APIs is not implemented. For MVP, the presentation layer can enumerate the calls to the backend.

## Hacks
1. Chose H2 as the database. It's in-memory and good for MVP. It helps to avoid setting up a database for MVP purposes. Easy to switch to desired DB once the prototype comes to a good shape. 

## Run

### Locally

### Remote


## Tests

### API tests

Add Users (POST)

```
curl --location 'http://localhost:8080/v1/users' \
--header 'Content-Type: application/json' \
--data-raw '[
{
"firstName": "fname1",
"lastName": "lname1",
"emailId": "fname1.lname1@xyz.com"
},
{
"firstName": "fname2",
"lastName": "lname2",
"emailId": "fname2.lname2@xyz.com"
}
]'
```

Get users (GET)

```
curl --location 'http://localhost:8080/v1/users'
```

Get user by email (GET)

```
curl --location 'http://localhost:8080/v1/user?email=fname2.lname2%40xyz.com'
```

Add availability for a user (POST)

```
curl --location 'http://localhost:8080/v1/availability' \
--header 'Content-Type: application/json' \
--data '[
    {
        "_date": "2023-07-03",
        "_start": "15:30:00",
        "_end": "16:30:00",
        "userid": 1
    }
]'
```
```
curl --location 'http://localhost:8080/v1/availability' \
--data '[
    {
        "_date": "2023-07-03",
        "_start": "19:00:00",
        "_end": "19:30:00",
        "userid": 2
    }
]'
```

Show availability for a user (GET)

```
curl --location 'http://localhost:8080/v1/availability?user_id=1&date=2023-07-03'
```

Show overlap between two users for a date (GET)

```
curl --location 'http://localhost:8080/v1/overlap?user1=1&user2=2&date=2023-07-03'
```

Add recurring availability for a user (GET)

```
curl --location 'http://localhost:8080/v1/recurring' \
--header 'Content-Type: application/json' \
--data '
{
"startdate": "2023-07-03",
"_start": "20:40:00",
"_end": "21:10:00",
"userid": 1,
"interval": "weekly",
"occurrences": 3
}
'
```

Deleting availability for a user (DELETE)

```
curl --location --request DELETE 'http://localhost:8080/v1/availability' \
--header 'Content-Type: application/json' \
--data '
{
"_date": "2023-07-03",
"_start": "20:40:00",
"_end": "21:00:00",
"userid": 1
}
'
```

Check updated availability for above user after deleting availability (GET)

```
curl --location 'http://localhost:8080/v1/availability?user_id=1&date=2023-07-03'
```

### Import below collection into Postman for full collection of APIs.

https://api.postman.com/collections/27446545-3d6efec7-509d-40b9-a235-61ec409c4055?access_key=PMAT-01H4R5AY87YCD1D5DHMHE7SEQR


### Unit tests
mvn clean test

### Metrics

```
curl --location 'http://localhost:8080/actuator/metrics/http.server.requests?tag=uri%3A%2Fv1%2Fusers'
```

```
curl --location 'http://localhost:8080/actuator/metrics/http.server.requests?tag=uri%3A%2Fv1%2Fusers&tag=status%3A201'
```

```
curl --location 'http://localhost:8080/actuator/metrics/http.server.requests?tag=uri%3A%2Fv1%2Fusers&tag=method%3APOST'
```