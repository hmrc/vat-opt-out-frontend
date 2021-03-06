# vat-opt-out-frontend

[![Build Status](https://travis-ci.org/hmrc/vat-opt-out-frontend.svg)](https://travis-ci.org/hmrc/vat-opt-out-frontend) [ ![Download](https://api.bintray.com/packages/hmrc/releases/vat-opt-out-frontend/images/download.svg) ](https://bintray.com/hmrc/releases/vat-opt-out-frontend/_latestVersion)


## Summary

This is the repository for VAT Opt Out Frontend.

This service provides the functionality to opt out of submitting VAT returns through software where eligible.

## Requirements

This service is written in [Scala](http://www.scala-lang.org/) and [Play](http://playframework.com/), so needs at least a [JRE](https://www.java.com/en/download/) to run.

## Running the application

In order to run this microservice, you must have SBT installed. You should then be able to start the application using:

`sbt "run 9166"`

## Testing

Use the following command to run unit and integration tests:

`sbt test it:test`

## License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").