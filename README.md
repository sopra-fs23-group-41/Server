# README

<!-- TABLE OF CONTENTS -->

## Introduction 
Our project began from the keyword online shopping, which has various merits like lower price, more products and so on.
But it's still quite tedious, boring, and hard to get an intuitive understanding on price.
So, we wanted to create an intuitive and fun quiz game where the users have to guess the price of Asos products. 
Show me the money!

<div style="text-align: center;">
    <div style="display: inline-flex">
        <img src="img.png" alt="Image 1" width="200" />
        <img src="img_1.png" alt="Image 2" width="200" />
        <img src="img_2.png" alt="Image 3" width="200" />
    </div>
</div>


## Technologies
The back-end of the application is developed using Java and utilizes the Spring Boot framework. 
The data persistence is managed through JPA (Java Persistence API) with Hibernate as the underlying ORM (Object-Relational Mapping) tool. 
The application is deployed to the Google Cloud App Engine using GitHub actions for seamless deployment and version control.


## High-level components
The controller classes handle the REST calls and pass them to the Services. Their main role is to facilitate communication between the client and the server to ensure the game's functionalities.
The service classes receive the calls from the controller and perform the necessary operations. They are responsible for executing the requested actions and manipulating the Game.class.
The [Game.class](https://github.com/sopra-fs23-group-41/Server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs23/entity/Game.java) serves as the heart of the game. It is responsible for initializing and managing the minigames based on specified settings.
Within each minigame questions are generated by calling the [ASOS API](https://rapidapi.com/apidojo/api/asos2), converting the response into an Article and further transforming the article into a question format.


## Launch & Deployment

### Build

```bash
./gradlew build
```

### Run

```bash
./gradlew bootRun
```

### Test

```bash
./gradlew test
```
Releases can be done via GitHub.
## Roadmap
+ Version of Guess The Price, where players have to type in a price
+ Stronger authentication
+ Top-notch synchronization (WebSockets)
+ Multiplayer all-time leaderboard, based on points snatched during games
+ Friends feature
+ Permanently store registered users

## Authors and acknowledgment
+ Eunji Lee ([@EunjiLee-dev](https://github.com/EunjiLee-dev)): Front-end
+ Laurent Le Febve ([@LaurentLeFebve](https://github.com/LaurentLeFebve)): Front-end
+ Tiago Ferreiro Matos ([@tyagos](https://github.com/tyagos)): Front-end
+ Timo Tietje ([@TimoTietje](https://github.com/TimoTietje)): Back-end
+ Yuqing Huang ([@DarleneQing](https://github.com/DarleneQing)): Back-end

  and special thanks to our personal TA: Dennys Huber ([@devnnys](https://github.com/devnnys))

## License
[Apache License 2.0](LICENSE)
