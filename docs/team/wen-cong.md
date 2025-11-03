---
layout: page
title: Wen Cong's Project Portfolio Page
---

### Project: DevBooks

DevBooks provides fast digital access to students in National University of Singapore, School of Computing, making it easier to contact any student using their preferred mode of communication. Allow students to find project mates from the same project group easily and view the development profile of their contact.

Given below are my contributions to the project:

* **Major Enhancements: Enhanced Contact Model**
    * Modified the core contact model to support new fields, specifically **Telegram** and **GitHub** profiles.
    * While the concept was simple, this enhancement was time-consuming as it required propagating changes throughout the application, including the model, storage, and multiple existing commands.

* **New Feature: Pin / Unpin Contact**
    * Implemented the `pin` and `unpin` features to allow users to "favorite" contacts and keep them at the top of the contact list for easy access.
    * **Challenge:** The most significant challenge was developing the custom sorting algorithm. This algorithm had to ensure pinned contacts always appeared first, while the remaining unpinned contacts were still sorted correctly according to the user's previously selected order.

* **New Feature: Mass Delete Tag**
    * Implemented the `delete-tag` command (placeholder name, adjust if needed) to allow users to remove a specific tag from all contacts who have it.
    * This feature was implemented efficiently by adapting and reusing existing logic from the rename tag feature.

* **Bug Fixes and Improvements**
* Resolved various minor bugs and made improvements to enhance the overall user experience and application stability.
