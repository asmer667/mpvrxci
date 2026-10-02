# mpvRx 2.7.2 — Neon Cinematic UI update

This source update keeps the existing local media repositories, playback actions, navigation destinations, settings, playlist management, and player implementation. It changes presentation and adds a real local-history carousel.

## Implemented in source

- Default theme uses midnight navy with electric blue, ultraviolet, and magenta accents; the light palette remains softer for contrast.
- Video and folder cards receive a three-pass gradient glass edge plus elevation, while their original click, long-press, selection, swipe, and playback callbacks remain connected.
- The music grid's song, album, artist, and playlist cards use the same visual language.
- The home folder browser now includes a horizontal, snap-scrolling `Recently played` carousel backed by the app's existing `RecentlyPlayedViewModel`. It filters to local file paths, loads thumbnails lazily through `ThumbnailRepository`, highlights the card nearest the viewport center, and opens the selected item through the existing `MediaUtils.playFile` action.
- The carousel appears only when Recently Played is enabled and does not substitute demo or remote catalogue media.
- Folder thumbnail video lists are cached in-process by folder path and modification signature for 90 seconds to avoid re-querying the same folder on every navigation back into the browser. The existing thumbnail memory/disk caches and IO dispatch remain in use.
- Main-tab horizontal paging, music source selection, settings destinations, history actions, and local folder navigation remain on the original code paths.

## Performance design

- Carousel artwork is requested only for composed/visible LazyRow items; memory cache is checked synchronously first and thumbnail generation runs on `Dispatchers.IO`.
- Folder content lookup for folder-cover thumbnails uses a short-lived in-memory cache keyed by path and modification signature.
- No blur modifier is added to every grid cell; the card treatment uses lightweight gradient strokes and layer elevation to avoid an expensive blur pass per thumbnail.

## Build status

Gradle was intentionally not run in this environment at the user's request. The source archive is supplied for local compilation and device testing. A successful Gradle build, APK runtime validation, and performance measurement have therefore not been established here.
