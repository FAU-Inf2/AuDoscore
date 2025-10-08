[![Build Status](https://travis-ci.org/FAU-Inf2/AuDoscore.svg?branch=master)](https://travis-ci.org/FAU-Inf2/AuDoscore)

What is AuDoscore?
=======
AuDoscore is an extension of JUnit that is used to evaluate Java programming exercises.

We use AuDoscore in our algorithm and data structure course to automate evaluation of student homework submissions.

Available Annotations
=======

| Annotation          | Required | Position                     | Arguments                                                                                                                  | Semantics                                                                                                                                                                                                                                                                                                                              |
|---------------------|:--------:|------------------------------|----------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `@Exercises`        |   Yes    | class level                  | Array of `@Ex` annotations                                                                                                 | contains a list of `@Ex` annotations; see `@Ex`                                                                                                                                                                                                                                                                                        |
| `@Ex`               |   Yes    | element in `@Exercises` list | `exId`: String, `points`: Double                                                                                           | for every (sub)exercise, create an `@Ex` annotation with unique `exID`                                                                                                                                                                                                                                                                 |
| `@Points`           |   Yes    | test case level (method)     | `exId`: String, `bonus`: Double, `malus`: Double, `comment`: String, defaults to method name                               | Student earns `bonus` / sum(`bonus`) * `Ex.points` points for passing this test case. Student looses `malus` / sum(`bonus`) * `Ex.points` points for *not* passing this test case.                                                                                                                                                     |
| `@Forbidden`        |    No    | class level                  | `value`: Array of Strings, `type`: `Forbidden.Type.PREFIX` (default), `Forbidden.Type.FIXED`, or `Forbidden.Type.WILDCARD` | Arguments specify forbidden classes/methods/etc., depending on the `type`. `PREFIX`: Arguments are interpreted as prefix to forbidden elements. `FIXED`: Arguments are exactly the forbidden elements. `WILDCARD`: Arguments are interpreted regex-like (see remarks below!). See also `@NotForbidden`.                                |
| `@NotForbidden`     |    No    | class level                  | `value`: Array of Strings, `type`: `Forbidden.Type.PREFIX` (default), `Forbidden.Type.FIXED`, or `Forbidden.Type.WILDCARD` | Arguments specify allowed classes/methods/etc., these take precedence over `@Forbidden`. See also `@Forbidden`.                                                                                                                                                                                                                        |
| `@Replace`          |    No    | test case level (method)     | Array of Strings                                                                                                           | Strings refer to methods in the student's code. For this test case, all methods mentioned in the `@Replace` annotation will be replaced with their cleanroom counterparts. Note: `@Replace` can only be used in secret tests!                                                                                                          |
| `@SecretClass`      |    No    | class level                  | None                                                                                                                       | Marks a test class to be secret. Results will not be shown to students before the submission deadline.                                                                                                                                                                                                                                 |
| `@CompareInterface` |    No    | class level                  | Array of String                                                                                                            | Checks if methods and fields of students have the same signature as their cleanroom counterparts. Possible Strings: "Classname.Methodname, "Classname.Fieldname", "Classname". If only the Classname is given all public methods/fields are checked.                                                                                   |
| `@InitializeOnce`   |    No    | static field                 | `value`: String                                                                                                            | At first execution, the method given as `value` is called and its result is stored in a temporary file. At subsequent executions, the annotated attribute is initialized using the precomputed result from the file. Note: `@InitializeOnce` can only be used in secret tests and the result of the given method must be serializable! |

Remarks on `Forbidden.Type.WILDCARD`:
-----
- `Type.WILDCARD` can be used with Scala-Regex-like patterns:
  - Scala-Regex "delegates to the java.util.regex package of the Java Platform."
  - "See the documentation for java.util.regex.Pattern for details about the regular expression syntax for pattern strings."
- **but with following SPECIAL treatments:**
  - `.` (regex: "any character") is escaped to `\.` (literally: e.g. `org.junit.Test`) - so no need to escape in `@Forbidden`/`@NotForbidden`
  - `$` (regex: "end of a line") is escaped to `\$` (literally: e.g. companion `LazyList$`) - so no need to escape in `@Forbidden`/`@NotForbidden`
  - `X*Y` (regex: "X, zero or more times") is replaced by `X[^.\$]*Y` (matching any character except `.` or `$`: e.g. `List*` matches `List`,`ListMap`,`ListSet` - **but not** `List.map`,`List$.empty`,...!)
  + `**` is replaced by `.*` (regex: "any character" + "zero or more times": e.g. `List**` matches `List`,`ListMap`,...`List.map`,...,`List$.empty`,`List$.apply`,...)
- Consider the specialties above when declaring your own regex patterns in `@Forbidden`/`@NotForbidden`!


Local Usage
=======

AuDoscore comes with a test script that can be used to locally grade a submission.
The test script requires that the files belonging to an exercise are organized in a specific directory structure:

```
exercise_dir
  ↦ cleanroom
      ↦ Solver.java
  ↦ interfaces
      ↦ ISolver.java
  ↦ junit
      ↦ PublicTest.java
      ↦ SecretTest.java
  ↦ skeleton
      ↦ Solver.java
  ↦ student
      ↦ Solver.java
```

The `cleanroom` directory contains the cleanroom solution (i.e. the sample solution from the lecturer).
The annotated unit tests reside in the `junit` directory.
The `interfaces` directory is optional and contains the interfaces to be implemented by a student submission.
The `skeleton` directory is optional and contains skeleton code provided to the students
(thus it is ignored by the AuDoscore infrastructure).
A student submission can optionally be placed in the `student` directory
(if placed in another folder, this folder must be explicitly provided as argument to the test script).

To run AuDoscore on this example, `cd` to the `exercise_dir` and execute the `test.sh` script from the AuDoscore repository.
The `test.sh` script takes the name of the directory containing the submission as an optional parameter,
so you can also test submissions in other directories than `student` (e.g. the `cleanroom` itself).
The test script produces a nicely formatted output of the passed and failed tests and also prints the scored points.

Branches
=======

There might be several active branches in this repository.
Development usually happens in the `master` branch.
The versions of AuDoscore in use by us are tagged or packaged under `Releases`.

License
=======

The project is licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

Contact
=======

Please report bugs via the issue tracker.

Feedback: [cs2-aud@fau.de](mailto:cs2-aud@fau.de)
