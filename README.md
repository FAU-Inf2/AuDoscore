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
| `@Forbidden` | No | class level | `value`: Array of Strings, `type`: Forbidden.Type.PREFIX (default), Forbidden.Type.FIXED, or Forbidden.Type.WILDCARD | Arguments specify forbidden classes/methods/etc., depending on the `type`. PREFIX: Arguments are interpreted as prefix to forbidden elements. FIXED: Arguments are exactly the forbidden elements. WILDCARD: Like PREFIX but also supports wildcards. See also `@NotForbidden` |
| `@NotForbidden` | No | class level | `value`: Array of Strings, `type`: Forbidden.Type.PREFIX (default), Forbidden.Type.FIXED, or Forbidden.Type.WILDCARD | Arguments specify allowed classes/methods/etc., these take precedence over `@Forbidden`; see also `@Forbidden` |
| `@Points` (combination of `@Malus` and `@Bonus` ) | sort of | test case level | `exId`: String, `bonus`: Double, `malus`: Double, `comment`: String, defaults to method name | Student earns `bonus` / sum(`bonus`) * `Ex.points` points for passing this test case. Student looses `malus` / sum(`bonus`) * `Ex.points` points for *not* passing this test case |
| `@Bonus`(deprecated, please use `@Points`) | sort of | test case level | `exId`: String, `bonus`: Double, `comment`: String, defaults to method name | Student earns `bonus` / sum(`bonus`) * `Ex.points` points for passing this test case |
| `@Malus` (deprecated, please use `@Points`) | sort of | test case level | `exId`: String, `malus`: Double, `comment`: String, defaults to method name | Student looses `malus` / sum(`bonus`) * `Ex.points` points for *not* passing this test case |
| `@Replace` | No | test case level | Array of Strings | Strings refer to methods in the student's code. For this test case, all methods mentioned in the `@Replace` annotation will be replaced with their cleanroom counterparts. Note: `@Replace` can only be used in secret tests|
| `@SecretClass` | No | class level | None | Marks a test class to be secret. Results will not be shown to students before the submission deadline. |
| `@CompareInterface` | No | class level | Array of String | Checks if methods and fields of students have the same signature as their cleanroom counterparts. Possible Strings: "Classname.Methodname, "Classname.Fieldname", "Classname". If only the Classname is given all public methods/fields are checked.|

Local Usage
=======

AuDoscore comes with a test script which can be used to locally grade a
submission. The test script requires that the files belonging to an exercise be
organized in a specific directory structure:

```
exercise_dir
  ↦ cleanroom
      ↦ Solver.java
  ↦ interfaces
      ↦ ISolver.java
  ↦ junit
      ↦ UnitTest.java
      ↦ SecretTest.java
  ↦ skeleton
      ↦ Solver.java
  ↦ student
      ↦ Solver.java
```

The `cleanroom` directory contains the cleanroom solution. The annotated unit
tests reside in the `junit` directory, while the student's submission is placed
in the `student` directory. The `interfaces` and `skeleton` directories are
optional and contain the interfaces to implement by a submission and the
skeleton code for a submission, respectively.

To run AuDoscore on this example, `cd` to the `exercise_dir` and execute the
`test.sh` script from the AuDoscore repository. The `test.sh` script takes the
name of the directory containing the submission as an optional parameter, so
you can also test submissions in other directories than `student`. The test
script produces a nicely formatted output of the passed and failed tests and
also prints the scored points.

Branches
=======

There are currently two active branches in this repository. Development usually
happens in the `master` branch. The `release` branch just contains the version
of AuDoscore currently in use by us.

License
=======

The project is licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

Contact
=======

Please report bugs via the issue tracker.

Feedback: [cs2-aud@fau.de](mailto:cs2-aud@fau.de)

