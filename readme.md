Short description of the Project

I've made a simple app that contains one screen with a list of ads where you can add favorites and toggle to hide non-favorites. Each item in the list contains of an image, price, location and title.

The UI is written in Compose and I've used MVVM with repositories. I've not included any dependency injection library because of the size of this project. I'm using Room to persist ads and favorites. I'm using the database as the single source of truth. The data flow is network -> database -> view. I've also added some tests. Mainly for the repositories. You can find them in "androidTest". I'm running the tests on device because of Room

Proud of:
Not pround but fun writing all the UI in Compose

What else would you have done with more time:

- Test AdViewModel
- Animations 


FYI I've used Android Studio Eel
