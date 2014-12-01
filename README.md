[![Build Status](https://travis-ci.org/FAU-Inf2/AuDoscore.svg?branch=master)](https://travis-ci.org/FAU-Inf2/AuDoscore)

What is AuDoscore?
=======
AuDoscore is an extension of JUnit that is used to evaluate Java programming exercises.

We use AuDoscore in our algorithm and data structure course in combination with
[DOMjudge](http://www.domjudge.org) to automate evaluation of student's
submissions.

Available Annotations
=======

| Annotation | Required | Position | Arguments | Semantics |
|------------|:--------:|----------|-----------|-----------|
| `@Exercises` | Yes    | class level | Array of `@Ex` annotations | contains a list of `@Ex` annotations; see `@Ex` |
| `@Ex` | Yes | element in `@Exercises` list | `exId`: String, `points`: Double, `comment`: String, defaults to `<n.a.>` | for every (sub)exercise, create an `@Ex` annotation with unique `exID` |
| `@Forbidden` | No | class level | Array of Strings | Arguments are interpreted as prefixes of forbidden classes/methods/etc., see also `@NotForbidden` |
| `@NotForbidden` | No | class level | Array of Strings | Arguments are interpreted as prefixes of allowed classes/methods/etc., these take precedence over `@Forbidden`; see also `@Forbidden` |
| `@Bonus`(deprecated, please use `@Points`) | sort of | test case level | `exId`: String, `bonus`: Double, `comment`: String, defaults to method name | Student earns `bonus` / sum(`bonus`) * `Ex.points` points for passing this test case |
| `@Malus` (deprecated, please use `@Points`) | sort of | test case level | `exId`: String, `malus`: Double, `comment`: String, defaults to method name | Student looses `malus` / sum(`bonus`) * `Ex.points` points for *not* passing this test case |
| `@Replace` | No | test case level | Array of Strings | Strings refer to methods in the student's code. For this test case, all methods mentioned in the `@Replace` annotation will be replaced with their cleanroom counterparts. |
| `@SecretCase` | No | test case level | None | Result will not be shown to the students before submission deadline. Note that there should be a secret and a non-secret test case for every `exID/@Ex` |
| `@Points` (combination of `@Malus` and `@Bonus` ) | sort of | test case level | `exId`: String, `bonus`: Double, `malus`: Double, `comment`: String, defaults to method name | Student earns `bonus` / sum(`bonus`) * `Ex.points` points for passing this test case. Student looses `malus` / sum(`bonus`) * `Ex.points` points for *not* passing this test case |

License
=======

The project is licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

Contact
=======

Please report bugs via the issue tracker.

Feedback: [aud@i2.cs.fau.de](mailto:aud@i2.cs.fau.de)

