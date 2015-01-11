The `ivy` sub-project of sbt doesn't really depend on the other
sub-projects of sbt (but for `io`). Here it is, just it and `io`.

The same applies to the `classpath` project, which provides
`ClassLoader` and class path related utilities. This branch
also contains a version of it, lighter then the original though.
