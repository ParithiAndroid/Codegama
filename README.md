# CG News App

A simple Android news app to fetch and display the latest news articles using the NewsAPI. Users can search for news topics and read full articles in a WebView.

---

## Features

- Search for news articles.
- Display news articles with title, description, image, and publication date.
- Open full articles in a WebView.
- Clear search input using end icon.
- Smooth RecyclerView list with click functionality.
- Account settings

---

## Demo video

<a href="https://drive.google.com/file/d/1urR09Lx8m_6K2n1Ayd7-QEWcpW0s6UP8/view?usp=sharing" target="blank">https://drive.google.com/file/d/1urR09Lx8m_6K2n1Ayd7-QEWcpW0s6UP8/view?usp=sharing</a>

---

---

## Download APK

<a href="https://drive.google.com/file/d/122rnh-MiBHD9z-4mEZZ_S9xSKWdCz75J/view?usp=drive_link" target="blank">https://drive.google.com/file/d/122rnh-MiBHD9z-4mEZZ_S9xSKWdCz75J/view?usp=drive_link</a>

---

## Project Setup

1. Clone the repository:

```bash
git clone https://github.com/yourusername/cg-news-app.git
cd cg-news-app
```

Open the project in Android Studio.

Set up your API key (see API Key Setup).

Build and run the app on an emulator or physical device.

API Key Setup

The app uses NewsAPI
 for fetching news articles.

Sign up and get your free API key from NewsAPI
.

Open local.properties (or create it if it doesn't exist) in the project root.

Add the following line:
NEWS_API_KEY=your_api_key_here
The app automatically uses this key via BuildConfig.NEWS_API_KEY.

Dependencies Used

- Retrofit + Okhttp – For networking and API calls
- Glide – For image loading
- Material Components – For modern UI elements (TextInputLayout, buttons)
- RecyclerView – To display the news list
- Kotlin Coroutines & Flow – For asynchronous operations
- Room - For local caching
- Hilt - dependency injection
- swipetorefresh and shimmer
- Workmanager - For background task
- Firebase Auth
