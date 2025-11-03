---
layout: page
title: Thaddaeus's Project Portfolio Page
---

### Project: DevBooks

DevBooks is a desktop address book application used for teaching Software Engineering principles. The user interacts with it using a CLI, and it has a GUI created with JavaFX. It is written in Java, and has about 10 kLoC.

**Feature Enhancements**:
* Updated Edit Feature:
    * Remove volatile add/removing of tags
* Enhanced List Feature:
    * Added sorting feature by alphabetical/recent order

New Features:
* Launch Feature:
    * Allows launching of communication mode.
* Rename Tag
    * Allows mass editing of tags for multiple users in the displayed list

Testing:
* Introduced `Mockito` as a test dependency mock Desktop behaviour by facilitating unit testing of system-level interaction
  This allows my newly added test cases to simulate success and failure of the ApplicationLinkLauncher without having to actually open the browser or mail application for testing.
