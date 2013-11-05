Explosions
======================
[![Build Status](https://travis-ci.org/Talon876/explosions.png?branch=master)](https://travis-ci.org/Talon876/explosions)

[Download](http://nolat.org/downloads/explosion.jar) latest version.

A chain reaction game with some secrets.

Eclipse Setup
-------------
Clone the repository to your eclipse workspace and open a shell window to this folder.

Execute `gradlew eclipse`. The first time this runs it will download Gradle to a .gradle folder in the repository, then it will build eclipse configuration files that allow you to import it as a project.

There are two projects: explosions-core and explosions-desktop. explosions-core contains all of the main game code and explosions-desktop depends on explosions-core but only contains code to launch the game in a desktop environment.

If any changes are made to the dependencies in the build.gradle file, you will have to rerun `gradlew eclipse` and then refresh the project in eclipse.

Debug Mode
----------
Debug mode can be enabled by passing "debug" as the first program argument in your launch configuration. This bypasses the title/main menu screens and disables music.

Attribution
-----------

[Cherry Blossom](http://incompetech.com/music/royalty-free/?keywords=cherry&Search=Search) - Kevin MacLeod (incompetech.com)
[Whoosh puff](http://www.freesound.org/people/Speedenza/sounds/168109/) - Speedenza