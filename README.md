## MovieMania
* This is first project android nanodegree at udacity.
* This project uses [butterknife](http://jakewharton.github.io/butterknife/) library.
* It fetches data from [this](https://www.themoviedb.org/) website.

## Feature Implemented
* ### UI-Layout
  * Movies are displayed in mainlayout via a grid of their corresponding movie poster
  * UI contains menu to sort the thumbnails depending upon highest rated, popular and favorite movies.
  * UI contains a screen for displaying the details for a selected movie.
  * Movie details layout contains:-
    * Title
    * Release date
    * movie poster
    * vote average
    * plot synopsis
    * Trailer
    * Reviews
  
* ### UI-Function
  * When the user changes the sort criteria(most popular, highest rated and favorites) the main view gets updated correctly.
  * When a movie poster thumbnail is selected, the movie details screen is launched or displayed in fragment on case of tablet.
  * When trailer is selected app uses an **intent** to launch a trailer.
  * In the movies detail screen, a user can tap a button to mark it as a favorite.

* ### Data Persistence
  * App uses **ContentProvider** to populate favorite movie details.

* ### Sharing Functionality
  * Movie Details View includes an Action Bar item that allows the user to share the first trailer video URL from the list of trailers
  * App uses a share Intent to expose the external youtube URL for the trailer.
 
 ![Alt text](https://github.com/ad-os/MovieMania/blob/master/app/src/main/res/drawable/1.png)
![Alt text](https://github.com/ad-os/MovieMania/blob/master/app/src/main/res/drawable/4.png)
![Alt text](https://github.com/ad-os/MovieMania/blob/master/app/src/main/res/drawable/3.png)
![Alt text](https://github.com/ad-os/MovieMania/blob/master/app/src/main/res/drawable/2.png)
