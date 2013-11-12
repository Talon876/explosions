Explosions
======================
[![Build Status](https://travis-ci.org/Talon876/explosions.png?branch=master)](https://travis-ci.org/Talon876/explosions)

[Download](http://nolat.org/downloads/explosion.jar) latest version.

A chain reaction game with some secrets.

Screenshots
-----------
![Main Menu](http://i.imgur.com/ekoItz1.png)

![Levels](http://i.imgur.com/x4sCUnq.png)

![Gameplay](http://i.imgur.com/jsmxr6I.png)

Changelog
---------
*  Version 0.8.2
    * Added 3 "star" info to level select screen
    * Added score to highscores in addition to levels complete
    * fixed issue where last level wouldn't save the score
    * reduced popping volume
*  Version 0.8.1
    * first big release
    * online highscores

Eclipse Setup
-------------
Clone the repository to your eclipse workspace and open a shell window to this folder.

Execute `gradlew eclipse`. The first time this runs it will download Gradle to a .gradle folder in the repository, then it will build eclipse configuration files that allow you to import it as a project.

There are two projects:

* `explosions-core` - contains main game code
* `explosions-desktop` - contains assets folder and DesktopLauncher.java with main method for executing in desktop environments.

Any art/sound/font/etc assets need to be placed in the assets folder in the explosions-desktop project.

If any changes are made to the dependencies in the build.gradle file, you will have to rerun `gradlew eclipse` and then refresh the project in eclipse.

Debug Mode
----------

* Disables music
* Bypass title/main menu screens
* Enable advancing to the next level by pressing F12
* FPS counter shown by default (can be toggled with F10)

Debug mode can be enabled by passing "debug" as the first program argument in your launch configuration.

Attribution
-----------

[Cherry Blossom](http://incompetech.com/music/royalty-free/?keywords=cherry&Search=Search) - Kevin MacLeod (incompetech.com)

[Whoosh puff](http://www.freesound.org/people/Speedenza/sounds/168109/) - Speedenza
