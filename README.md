DJEclipse <a href="https://twitter.com/DJEclipsePlugin" class="twitter-follow-button" data-show-count="false" data-size="large" data-show-screen-name="false">@DJEclipsePlugin</a> 
=========

An Eclipse plugin to integrate the Class File Reader (CFR) java decompiler of [Lee Benfield](http://www.benf.org).

It works with Eclipse 3.8-4.2 (Juno releases) and above.

Installation
============

The Eclipse update site is located on [BinTray](http://dl.bintray.com/jplandrain/djeclipse). You can copy and paste the URL directly into your Eclipse IDE.

You can also download releases from the [continuous integration platform](http://djeclipse.ci.cloudbees.com/job/DJEclipse/) for offline installation.

Archived releases can be obtained from the [maven repository](http://repository-djeclipse.forge.cloudbees.com/release/org/nidget/eclipse/djeclipse/org.nidget.eclipse.djeclipse.p2updatesite/).

Changelog
=========
##### 0.0.8 (2016-05-23)
* Updated CFR from version 0.110 to version 0.115

##### 0.0.7 (2015-12-03)
* Updated CFR from version 0.103 to version 0.110

##### 0.0.6 (2015-09-16)
* Updated CFR from version 0.97 to version 0.103

##### 0.0.5 (2015-03-02)
* Updated CFR from version 0.88 to version 0.97

##### 0.0.4 (2014-10-28)
* Updated CFR from version 0.87 to version 0.88

##### 0.0.3 (2014-08-26)
* Updated CFR from version 0.80 to version 0.87
* Eclipse Update Site available on BinTray

##### 0.0.2
* Updated CFR from version 0.38 to version 0.80
* Added Eclipse 4.5 ("Eclipse Mars") profile

##### 0.0.1
* This very unstable preliminary version integrates the CFR decompiler of [Lee Benfield](http://www.benf.org).

Roadmap
======= 

- Java editor instead of a Text editor
- Improve the usability: control the settings of the decompiler.

Continuous Integration
======================

* ### [Travis CI](https://travis-ci.org/jplandrain/djeclipse "Travis CI") [![Build Status](https://travis-ci.org/jplandrain/djeclipse.svg?branch=master)](https://travis-ci.org/jplandrain/djeclipse)

* ### [DEV@cloud](https://djeclipse.ci.cloudbees.com/job/DJEclipse/ "DEV@cloud") [![Build Status](https://djeclipse.ci.cloudbees.com/buildStatus/icon?job=DJEclipse)](http://djeclipse.ci.cloudbees.com/job/DJEclipse/)

DJEclipse is a Free and Open Source Software (FOSS) project hosted gracefully on CloudBees [DEV@cloud](http://www.cloudbees.com/dev.cb "DEV@cloud").

![CloudBees DEV@cloud logo](http://web-static-cloudfront.s3.amazonaws.com/images/badges/BuiltOnDEV.png)

Screenshots
===========
Decompiling a simple class

![context menu screenshot1](https://github.com/jplandrain/djeclipse/blob/gh-pages/screenshots/djeclipse-capture1.png)

Decompiling a class loaded from a JAR library

![context menu screenshot2](https://github.com/jplandrain/djeclipse/blob/gh-pages/screenshots/djeclipse-capture2.png)
