CWAC Cache: Dealing With Pesky Download Times
=============================================

Many Android applications need to download stuff off of the
Internet, particularly Web assets like feeds and images. This
module contains a lightweight cache implementation, in the
form of an abstract class (`AsyncCache`) and one concrete
cache (`WebImageCache`).

As the name suggests, `AsyncCache` is designed for the case where
what you want might not already be available, either in RAM
(held in a `Map` with `SoftReference`s) or on disk (in a cache
directory of your choosing). In that case, you get handed
back some stub object (e.g., a placeholder image), while the
cache busily downloads what you need in the background.
Courtesy of integration with the [CWAC Bus module][bus], you
can be notified when the background download is complete, so
you can take appropriate action (e.g., replace the thumbnail).

`WebImageCache` is simply an `AsyncCache` that knows how to download
and cache images off the Web.

Usage
-----
Full instructions for using this module are forthcoming. Stay
tuned!

Dependencies
------------
This project requires the [CWAC Task module][task] and the
[CWAC Bus module][bus]. A copy of
compatible JARs can be found in the `libs/` directory of
the project, though you are welcome to try newer ones, or
ones that you have patched yourself.

Version
-------
This is version 0.1 of this module, meaning it is pretty darn
new.

Note that this module is undergoing some serious refactoring,
and so it is likely to massively change over the next month
or so.

Demo
----
In the `com.commonsware.cwac.cache.demo` package you will find
a sample activity that demonstrates the use of `WebImageCache`.

Note that when you build the JAR via `ant jar`, the sample
activity is not included, nor any resources -- only the
compiled classes for the actual library are put into the JAR.

License
-------
The code in this project is licensed under the Apache
Software License 2.0, per the terms of the included LICENSE
file.

Questions
---------
If you have questions regarding the use of this code, please
join and ask them on the [cw-android Google Group][gg]. Be sure to
indicate which CWAC module you have questions about.

[gg]: http://groups.google.com/group/cw-android
[task]: http://github.com/commonsguy/cwac-task/tree/master
[bus]: http://github.com/commonsguy/cwac-bus/tree/master