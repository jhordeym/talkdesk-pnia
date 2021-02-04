# pnia v1.1

This exercise is part of Talkdesk's recruitment process. Please do not share it
publicly.

It is up to you how much effort you put into it and exactly what you deliver. We
would expect you to spend two to three hours on it, and the result to be a piece
of working code that will comply exactly with the interface described below and
can be deployed by following one of the paths under the deployment section.

It is also up to you whether there's any need for documentation (or anything
else), depending on the problem you've been assigned or the method you chose to
solve it.

# Problem: phone number information aggregator

The goal of this exercise is to build a phone information aggregator API. This
system takes a list of phone numbers obtained from user input and returns the
count of valid phones broken down per prefix and per business sector.

For example, given 5 phones, 4 of them valid: one for Apple with prefix `+1`,
one for Bank of America with prefix `+1`, one for Quebramar with prefix
`+3519173`, and one for Salsa with prefix `+3519173`. In this case, the system
should return a count of 1 phone for Technology, and 1 phone for Banking
associated with the `+1` prefix, and a count of 2 phones for Clothing associated
with the `+3519173` prefix.

i.e.
```
$ curl -d '["+1983236248", "+1 7490276403", "001382355A", "+351917382672", "+35191734022"]' "http://challenge.example.com/aggregate"
{
  "1": {
    "Technology": 1
    "Banking": 1
  },
  "3159173": {
    "Clothing": 2,
  }
}
```

The following sections further specify the expected inputs and outputs of the
system, as well as of its dependencies.

## Resources

In order to simplify implementation, the system must leverage two resources that
are provided out of the box:

1. a file to obtain the prefix of the phone
2. an API to lookup the phone's business sector

### 1. Prefixes file

The file named `prefixes.txt` contains all eligible prefixes, separated by
new lines. Thus, given a file with the following contents:

```
12
18
3
```

And the numbers `+189192879, +129823978, +398123938`, then the prefix of the
first number is `18`, the prefix of the second number is `12`, and the prefix
of the third number is `3`. The file contains no overlapping prefixes
(e.g. `1` and `12`) and any number that cannot be mapped to an prefixes should
be considered invalid.

### 2. Business sector API

The business sector API can be found at
https://challenge-business-sector-api.meza.talkdeskstg.com. The API maps numbers
to their business sector (one of `Technology`, `Banking` or `Clothing`) and
provides number validation. Its latency SLA is lower than 1 second for 99.9% of
its requests.

The API represents numbers as resources under the `sector` prefix. It accepts GET
requests to endpoints that follow the `/sector/:number` pattern where `:number`
is a phone number. Responses for valid numbers will return a `200` http status, and include the
number canonically formatted as well as its sector. They comply with the schema:

```
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "type": "object",
  "items": {
    "type": "string",
    "properties": {
      "number": {
        "type": "string"
      },
      "sector": {
        "type": "string"
      }
    }
  },
  "required": ["number", "sector"]
}
```

For invalid numbers, the API replies with a bad request status code. A number is
considered valid if it contains only digits, an optional leading `+` and
whitespace anywhere except immediately after the `+`. A valid number has exactly
3 digits or more than 6 and less than 13. `00` is acceptable as replacement for
the leading `+`.

#### Business sector API example

Given number `+98 72 349` for a company in the Banking sector, a GET request
to `https://challenge-business-sector-api.meza.talkdeskstg.com/sector/+98%2072%20349` will return a `200` http response:

```
$ curl https://challenge-business-sector-api.meza.talkdeskstg.com/sector/+98%2072%20349
{
  "number": "+9872349",
  "sector": "Banking"
}
```

## Phone information aggregator API spec

The goal of this exercise id to build an API that exposes a single endpoint,
`/aggregate`. This endpoint must accept `POST` requests with a JSON array of
strings representing phone numbers, and return a JSON object as response. The
response object must contain one key per prefix present in the valid input
numbers, and each value corresponding to those keys must be another JSON object
mapping business sectors to their count of phones.

More specifically, the API must accept `POST` requests to the `/aggregate` endpoint
with a body that complies with the following schema:

```
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "type": "array",
  "items": {
    "type": "string"
  }
}
```

And its response body must comply with the following schema:

```
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "type": "object",
  "additionalProperties": {
    "type": "object",
    "additionalProperties": {
      "type": "number"
    }
  }
}
```

### Practical example of interaction with API

Given the following `prefixes.txt` file:

```
1
2
44
```

And the Business sector API returns the following replies:

```
$ curl https://challenge-business-sector-api.meza.talkdeskstg.com/sector/+1983248
{
  "number": "+1983248",
  "sector": "Technology"
}
```

```
$ curl https://challenge-business-sector-api.meza.talkdeskstg.com/sector/001382355
{
  "number": "+1382355",
  "sector": "Technology"
}
```

```
$ curl "https://challenge-business-sector-api.meza.talkdeskstg.com/sector/+147%208192"
{
  "number": "+1478192",
  "sector": "Clothing"
}
```

```
$ curl https://challenge-business-sector-api.meza.talkdeskstg.com/sector/+4439877
{
  "number": "+4439877",
  "sector": "Banking"
}
```

Then when `POST`ing the following to the phone number information aggregator API's `/aggregate` endpoint:

```
["+1983248", "001382355", "+147 8192", "+4439877"]
```

The API should return:

```
{
  "1": {
    "Technology": 2,
    "Clothing": 1
  },
  "44": {
    "Banking": 1
  }
}

```


## Deployment

To ensure the challenge will run in a consistent and controlled environment
while allowing for language and framework autonomy, we currently support two
deployment mechanisms, and you may choose whichever one works best for you.

### Docker

Through this route, please submit a tgz or zip with the project directory.
Running `docker-compose up web` on the project's root must start a web server
on port `8080` and be reachable on the host machine through
`http://localhost:8080`. To get started with docker / docker compose, we
recommend [this page](https://docs.docker.com/compose/gettingstarted/).

### Heroku

Through this route, please submit a tgz or zip with the project directory
containing a `.git/` repository that can be `git push`ed to heroku and render a
running web API. I.e. executing the following script should yield a running
project on https://td-project.herokuapp.com` (granted the user has access to
that heroku project)

```
tar -xzvf your_deliverable.tgz -C /tmp/project
cd /tmp/project
git remote add heroku https://git.heroku.com/td-project.herokuapp.git
git push heroku HEAD:master -f
```

Heroku supports a broad range of languages. For most of them, getting
something running is as simple as creating a `.git` repository, creating a
project on heroku and `git push`ing the code to heroku. To get started we
recommend [this page](https://devcenter.heroku.com/start).

## What happens after you submit the exercise

Part of this exercise's grading will be done through automatic tests. To make
sure the inputs and outputs of the API match the test's expectations, we have
bundled a simple `validate.sh` script that will exercise the interface and do a
basic test.

The automatic tests will be used to determine whether the exercise:
1. is immediately accepted (if it excels on the tests)
2. is immediately rejected (if it doesn't pass basic tests)
3. is manually reviewed (none of the above).

The manual review will value the same parameters we value on our day-to-day code
reviews:
* Clarity of the solution
* Correctness of the output
* Adequacy of the algorithms and data structures to the problem
* Adherence to best practices
* Error handling / resiliency
* Safety in the face of concurrency

In addition to the above, if the challenge is accepted, expect for it to be
explored in more detailed discussions in further stages of the hiring process.
