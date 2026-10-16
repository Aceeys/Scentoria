# Scentoria - A Fragrance Discovery (Android, Java + XML)

Phone app (portrait, Android 8.0+). Open in Android Studio: File > Open > select the `Scentoria` folder,
let Gradle sync, then press Run.

## Features from the project presentation -> where they live
| # | Feature                | Screen / file                                   |
|---|------------------------|-------------------------------------------------|
| 1 | Fragrance Search       | Discover tab - HomeFragment                     |
| 2 | Fragrance Information  | DetailActivity (notes, family, brand, description, longevity, projection) |
| 3 | Ratings & Reviews      | DetailActivity + ReviewStore                    |
| 4 | Favorites              | Favorites tab - FavoritesFragment               |
| 5 | Search & Filter        | FilterSheet: scent family, brand, occasion, PRICE RANGE, sort |
| 6 | Fragrance Comparison   | Compare tab - CompareFragment                   |
| 7 | Recommendations        | For You tab - ForYouFragment                    |
| 8 | User Profile           | Profile tab - ProfileFragment                   |

Data is a local sample catalogue in `PerfumeData.java` (prices are approximate PHP values).
Favorites, reviews and preferences are saved on the device with SharedPreferences.

## Catalogue
~1,200 real fragrances from 67 common brands (Creed, Xerjoff, Nishane, Louis Vuitton, Dior, Chanel, Versace, Tom Ford,
YSL, Armani, Gucci, Prada, Hermes, Guerlain, Jo Malone, PDM, MFK, Lattafa and more) in `assets/catalog.txt`.
Add a fragrance by APPENDING a line under its brand: `Name|Year|W/M/U|FL FR WO AM GO AQ CI SP`.
Verified-style notes for popular ones are in `assets/notes.txt`.

IMPORTANT: names, brands, gender and scent family come from the catalogue. Prices (~), longevity, projection, occasions
and (for most entries) notes/accords are ESTIMATES. Ratings start empty and come only from users' own reviews.
Check details against the brand before publishing anything.

## Bottle pictures
Put `<id>.jpg` in `app/src/main/assets/perfumes/` (see README.txt there) or set `Perfume.imageUrl`.
Without a picture the app shows the colored initial circle.
